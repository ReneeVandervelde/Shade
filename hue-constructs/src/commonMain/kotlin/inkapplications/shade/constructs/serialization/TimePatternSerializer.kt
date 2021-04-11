package inkapplications.shade.constructs.serialization

import com.soywiz.klock.*
import inkapplications.shade.constructs.LocalTime
import inkapplications.shade.constructs.TimePattern
import kotlinx.datetime.*
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.serializer
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
internal object TimePatternSerializer: DelegatedSerializer<TimePattern, String>() {
    override val backingSerializer: KSerializer<String> = String.serializer()

    override fun deserialize(value: String): TimePattern = when {
        value.matches(AbsoluteTimeTransformer.PATTERN) -> AbsoluteTimeTransformer.fromJson(value)
        value.matches(RandomizedTimeTransformer.PATTERN) -> RandomizedTimeTransformer.fromJson(value)
        value.matches(RecurringTimeTransformer.PATTERN) -> RecurringTimeTransformer.fromJson(value)
        value.matches(RecurringRandomizedTimeTransformer.PATTERN) -> RecurringRandomizedTimeTransformer.fromJson(value)
        value.matches(TimeIntervalTransformer.PATTERN) -> TimeIntervalTransformer.fromJson(value)
        value.matches(ExpiringTimerTransformer.PATTERN) -> ExpiringTimerTransformer.fromJson(value)
        value.matches(RandomExpiringTimerTransformer.PATTERN) -> RandomExpiringTimerTransformer.fromJson(value)
        value.matches(RecurringTimerTransformer.PATTERN) -> RecurringTimerTransformer.fromJson(value)
        value.matches(RandomRecurringTimerTransformer.PATTERN) -> RandomRecurringTimerTransformer.fromJson(value)
        else -> throw IllegalArgumentException("Unknown TimePattern: $value")
    }

    override fun serialize(value: TimePattern): String = when (value) {
        is TimePattern.AbsoluteTime -> AbsoluteTimeTransformer.toJson(value)
        is TimePattern.RandomizedTime -> RandomizedTimeTransformer.toJson(value)
        is TimePattern.RecurringTime -> RecurringTimeTransformer.toJson(value)
        is TimePattern.RecurringRandomizedTime -> RecurringRandomizedTimeTransformer.toJson(value)
        is TimePattern.TimeInterval -> TimeIntervalTransformer.toJson(value)
        is TimePattern.Timer.ExpiringTimer -> ExpiringTimerTransformer.toJson(value)
        is TimePattern.Timer.RandomExpiringTimer -> RandomExpiringTimerTransformer.toJson(value)
        is TimePattern.Timer.RecurringTimer -> RecurringTimerTransformer.toJson(value)
        is TimePattern.Timer.RandomRecurringTimer -> RandomRecurringTimerTransformer.toJson(value)
    }
}

/**
 * Handles Hue's fixed date/time objects.
 *
 * This is the least surprising of all of the time formats, but we
 * don't know what time zone it's in. So this is left as a LocalDateTime.
 * Good luck.
 */
object AbsoluteTimeTransformer {
    val PATTERN = Regex("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})")

    fun fromJson(data: String): TimePattern.AbsoluteTime = ISO8601.DATETIME_COMPLETE.parse(data)
        .utc
        .unixMillisLong
        .let(Instant::fromEpochMilliseconds)
        .toLocalDateTime(TimeZone.UTC)
        .let(TimePattern::AbsoluteTime)

    fun toJson(data: TimePattern.AbsoluteTime): String = data.time
        .toInstant(TimeZone.UTC)
        .toEpochMilliseconds()
        .let(DateTime::fromUnix)
        .format(ISO8601.DATETIME_COMPLETE)
}

/**
 * Handles a fixed time range.
 *
 * This is used to convey an event that happens semi-randomly
 * inside of a range of times.
 */
object RandomizedTimeTransformer {
    val PATTERN = Regex("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})A(\\d{2}):(\\d{2}):(\\d{2})")

    fun fromJson(data: String): TimePattern.RandomizedTime {
        val (dateString, startString, endString) = data.split('T', 'A')

        val date = LocalDate.parse(dateString)
        val startTime = ISO8601.TIME_LOCAL_COMPLETE.parseTime(startString)
        val endTime = ISO8601.TIME_LOCAL_COMPLETE.parseTime(endString)
        val start = date.atTime(startTime.hour, startTime.minute, startTime.second, 0)
        val end = date.atTime(endTime.hour, endTime.minute, endTime.second, 0)

        return TimePattern.RandomizedTime(start..end)
    }

    fun toJson(data: TimePattern.RandomizedTime): String {
        val start = data.range.start
            .toInstant(TimeZone.UTC)
            .toEpochMilliseconds()
            .let(DateTimeTz::fromUnixLocal)
        val end = data.range.endInclusive
            .toInstant(TimeZone.UTC)
            .toEpochMilliseconds()
            .let(DateTimeTz::fromUnixLocal)

        val dateString = ISO8601.DATE_CALENDAR_COMPLETE.format(start)
        val startString = ISO8601.TIME_LOCAL_COMPLETE.format(start.local.minus(start.local.startOfDay))
        val endString = ISO8601.TIME_LOCAL_COMPLETE.format(end.local.minus(end.local.startOfDay))

        return "${dateString}T${startString}A${endString}"
    }
}

/**
 * Handles a repeating event.
 *
 * This isn't documented at all, but the days of week are specified as
 * a three-digit binary flag 0-127 before the time.
 */
object RecurringTimeTransformer {
    val PATTERN = Regex("W(\\d{3})/T((\\d{2}):(\\d{2}):(\\d{2}))")

    fun fromJson(data: String): TimePattern.RecurringTime {
        val (daysString, timeString) = PATTERN.find(data)!!.destructured

        val time = ISO8601.TIME_LOCAL_COMPLETE.parseTime(timeString)
            .let { LocalTime(it.hour, it.minute, it.second) }
        val days = DayOfWeek.values()
            .filter { (daysString.toInt() and it.flag) == it.flag }
            .toSet()

        return TimePattern.RecurringTime(days, time)
    }

    fun toJson(data: TimePattern.RecurringTime): String {
        val time = data.time
            .let { Time(it.hour, it.minute, it.second, 0) }
            .format(ISO8601.TIME_LOCAL_COMPLETE)
        val days = data.days.map { it.flag }.sum()

        return "W%03d/T%s".format(days, time)
    }
}

/**
 * Handles a repeating event that occurs randomly inside a time range.
 *
 * This isn't documented at all, but the days of week are specified as
 * a three-digit binary flag 0-127 before the time.
 */
object RecurringRandomizedTimeTransformer {
    val PATTERN = Regex("W(\\d{3})/T((\\d{2}):(\\d{2}):(\\d{2}))A((\\d{2}):(\\d{2}):(\\d{2}))")

    fun fromJson(data: String): TimePattern.RecurringRandomizedTime {
        val parts = PATTERN.find(data)!!
        val daysString = parts.groups[1]?.value!!
        val startTimeString = parts.groups[2]?.value!!
        val endTimeString = parts.groups[6]?.value!!

        val startTime =  ISO8601.TIME_LOCAL_COMPLETE.parseTime(startTimeString)
            .let { LocalTime(it.hour, it.minute, it.second) }
        val endTime =  ISO8601.TIME_LOCAL_COMPLETE.parseTime(endTimeString)
            .let { LocalTime(it.hour, it.minute, it.second) }
        val days = DayOfWeek.values()
            .filter { (daysString.toInt() and it.flag) == it.flag }
            .toSet()

        return TimePattern.RecurringRandomizedTime(days, startTime..endTime)
    }

    fun toJson(data: TimePattern.RecurringRandomizedTime): String {
        val startTime = data.timeRange.start
            .let { Time(it.hour, it.minute, it.second, 0) }
            .format(ISO8601.TIME_LOCAL_COMPLETE)
        val endTime = data.timeRange.endInclusive
            .let { Time(it.hour, it.minute, it.second, 0) }
            .format(ISO8601.TIME_LOCAL_COMPLETE)
        val days = data.days.map { it.flag }.sum()

        return "W%03d/T%sA%s".format(days, startTime, endTime)
    }
}

/**
 * Handles a recurring time-span, which Hue calls a time interval.
 *
 * The days are optional, here and is nullable to preserve bijectivity.
 */
object TimeIntervalTransformer {
    val PATTERN = Regex("(W(\\d{3})/)?T((\\d{2}):(\\d{2}):(\\d{2}))/T((\\d{2}):(\\d{2}):(\\d{2}))")

    fun fromJson(data: String): TimePattern.TimeInterval {
        val parts = PATTERN.find(data)!!
        val daysString: String? = parts.groups[2]?.value
        val startTimeString = parts.groups[3]!!.value
        val endTimeString = parts.groups[7]!!.value

        val startTime =  ISO8601.TIME_LOCAL_COMPLETE.parseTime(startTimeString)
            .let { LocalTime(it.hour, it.minute, it.second) }
        val endTime =  ISO8601.TIME_LOCAL_COMPLETE.parseTime(endTimeString)
            .let { LocalTime(it.hour, it.minute, it.second) }
        val days = if (daysString == null) null else DayOfWeek.values()
            .filter { (daysString.toInt() and it.flag) == it.flag }
            .toSet()

        return TimePattern.TimeInterval(days, startTime..endTime)
    }

    fun toJson(data: TimePattern.TimeInterval): String {
        val startTime = data.timeRange.start
            .let { Time(it.hour, it.minute, it.second, 0) }
            .format(ISO8601.TIME_LOCAL_COMPLETE)
        val endTime = data.timeRange.endInclusive
            .let { Time(it.hour, it.minute, it.second, 0) }
            .format(ISO8601.TIME_LOCAL_COMPLETE)
        val days = data.days?.map { it.flag }?.sum()

        return if (days != null) "W%03d/T%s/T%s".format(days, startTime, endTime) else "T%s/T%s".format(startTime, endTime)
    }
}

/**
 * Handles a timer that expires after a given time.
 */
@OptIn(ExperimentalTime::class)
object ExpiringTimerTransformer {
    val PATTERN = Regex("PT((\\d{2}):(\\d{2}):(\\d{2}))")

    fun fromJson(data: String): TimePattern.Timer.ExpiringTimer {
        val (time) = PATTERN.find(data)!!.destructured

        return ISO8601.TIME_LOCAL_COMPLETE.parse(time)
            .millisecondsLong
            .milliseconds
            .let(TimePattern.Timer::ExpiringTimer)
    }

    fun toJson(data: TimePattern.Timer.ExpiringTimer): String {
        val time = data.expiration.inMilliseconds
            .let(::TimeSpan)
            .let(ISO8601.TIME_LOCAL_COMPLETE::format)

        return "PT%s".format(time)
    }
}

/**
 * Handles timers that expire randomly within a time range.
 */
@OptIn(ExperimentalTime::class)
object RandomExpiringTimerTransformer {
    val PATTERN = Regex("PT((\\d{2}):(\\d{2}):(\\d{2}))A((\\d{2}):(\\d{2}):(\\d{2}))")

    fun fromJson(data: String): TimePattern.Timer.RandomExpiringTimer {
        val parts = PATTERN.find(data)!!
        val startTimeString = parts.groups[1]!!.value
        val endTimeString = parts.groups[5]!!.value
        val startTime = ISO8601.TIME_LOCAL_COMPLETE.parse(startTimeString)
            .millisecondsLong
            .milliseconds
        val endTime = ISO8601.TIME_LOCAL_COMPLETE.parse(endTimeString)
            .millisecondsLong
            .milliseconds

        return TimePattern.Timer.RandomExpiringTimer(startTime..endTime)
    }

    fun toJson(data: TimePattern.Timer.RandomExpiringTimer): String {
        val startTime = data.expiration.start.inMilliseconds
            .let(::TimeSpan)
            .let(ISO8601.TIME_LOCAL_COMPLETE::format)

        val endTime = data.expiration.endInclusive.inMilliseconds
            .let(::TimeSpan)
            .let(ISO8601.TIME_LOCAL_COMPLETE::format)

        return "PT%sA%s".format(startTime, endTime)
    }
}

/**
 * Handles timers that repeat.
 */
@OptIn(ExperimentalTime::class)
object RecurringTimerTransformer {
    val PATTERN = Regex("R(\\d{2})?/PT((\\d{2}):(\\d{2}):(\\d{2}))")

    fun fromJson(data: String): TimePattern.Timer.RecurringTimer {
        val parts = PATTERN.find(data)!!
        val occurrences = parts.groups[1]?.value?.toInt()
        val time = ISO8601.TIME_LOCAL_COMPLETE.parse(parts.groups[2]!!.value)
            .millisecondsLong
            .milliseconds

        return TimePattern.Timer.RecurringTimer(occurrences, time)
    }

    fun toJson(data: TimePattern.Timer.RecurringTimer): String {
        val time = data.time.inMilliseconds
            .let(::TimeSpan)
            .let(ISO8601.TIME_LOCAL_COMPLETE::format)
        val occurrences = data.occurrences

        return if (occurrences != null) "R%02d/PT%s".format(occurrences, time) else  "R/PT%s".format(time)
    }
}

/**
 * Handles timers that repeat and expire randomly within a time range.
 */
@OptIn(ExperimentalTime::class)
object RandomRecurringTimerTransformer {
    val PATTERN = Regex("R(\\d{2})?/PT((\\d{2}):(\\d{2}):(\\d{2}))A((\\d{2}):(\\d{2}):(\\d{2}))")

    fun fromJson(data: String): TimePattern.Timer.RandomRecurringTimer {
        val parts = PATTERN.find(data)!!
        val occurrences = parts.groups[1]?.value?.toInt()
        val startTime = ISO8601.TIME_LOCAL_COMPLETE.parse(parts.groups[2]!!.value)
            .millisecondsLong
            .milliseconds
        val endTime = ISO8601.TIME_LOCAL_COMPLETE.parse(parts.groups[6]!!.value)
            .millisecondsLong
            .milliseconds

        return TimePattern.Timer.RandomRecurringTimer(occurrences, startTime..endTime)
    }

    fun toJson(data: TimePattern.Timer.RandomRecurringTimer): String {
        val startTime = data.timeRange.start.inMilliseconds
            .let(::TimeSpan)
            .let(ISO8601.TIME_LOCAL_COMPLETE::format)

        val endTime = data.timeRange.endInclusive.inMilliseconds
            .let(::TimeSpan)
            .let(ISO8601.TIME_LOCAL_COMPLETE::format)
        val occurrences = data.occurrences

        return if (occurrences != null) "R%02d/PT%sA%s".format(occurrences, startTime, endTime) else  "R/PT%sA%s".format(startTime, endTime)
    }
}

/**
 * Converts a standard DayOfWeek enum into Hue's Binary flags.
 */
internal val DayOfWeek.flag: Int get() = when(this) {
    DayOfWeek.SUNDAY -> 64
    DayOfWeek.MONDAY -> 32
    DayOfWeek.TUESDAY -> 16
    DayOfWeek.WEDNESDAY -> 8
    DayOfWeek.THURSDAY -> 4
    DayOfWeek.FRIDAY -> 2
    DayOfWeek.SATURDAY -> 1
}
