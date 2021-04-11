package inkapplications.shade.constructs

import inkapplications.shade.constructs.serialization.TimePatternSerializer
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

data class LocalTime(
    val hour: Int,
    val minute: Int,
    val second: Int,
): Comparable<LocalTime> {
    private val spanSeconds = second + (minute * 60) + (hour * 60 * 60)
    override fun compareTo(other: LocalTime): Int = spanSeconds.compareTo(other.spanSeconds)
}

/**
 * Abstraction of all of the various types of date/times that Hue handles.
 *
 * These are all documented exceptionally poorly in Hue's Documentation.
 * Please report any bugs found with this.
 */
@Serializable(with = TimePatternSerializer::class)
sealed class TimePattern {
    /**
     * An exact specified time.
     *
     * This time should be local to the hub. UTC times are deprecated in the Schedule API's
     */
    data class AbsoluteTime(
        val time: LocalDateTime
    ): TimePattern()

    /**
     * A time range when an event can happen randomly inside of.
     */
    data class RandomizedTime(
        val range: ClosedRange<LocalDateTime>,
    ): TimePattern() {
        @Deprecated(
            message = "Parameter consolidated to single LocalDateTime range",
            replaceWith = ReplaceWith("range"),
            level = DeprecationLevel.ERROR
        )
        val timeRange: Nothing get() = throw UnsupportedOperationException("")

        @Deprecated(
            message = "Parameter consolidated to single LocalDateTime range",
            replaceWith = ReplaceWith("range.start.date"),
            level = DeprecationLevel.ERROR
        )
        val date: Nothing get() = throw UnsupportedOperationException("")
    }

    /**
     * Every specified day of the week at a specified time.
     */
    data class RecurringTime(val days: Set<DayOfWeek>, val time: LocalTime): TimePattern()

    /**
     * Every specified day randomly between a range of times.
     */
    data class RecurringRandomizedTime(val days: Set<DayOfWeek>, val timeRange: ClosedRange<LocalTime>): TimePattern()

    /**
     * Every day *from* the start of the range to the end of the range.
     *
     * This could be easily confused with a `RecurringRandomizedTime`.
     * It is different in that this event *spans* the range from start
     * to end, as opposed to occurring randomly inside it.
     *
     * @param days The days this event should occur on. If null, it should occur daily.
     */
    data class TimeInterval(val days: Set<DayOfWeek>?, val timeRange: ClosedRange<LocalTime>): TimePattern()

    /**
     * Abstraction for the various types of timers.
     */
    @ExperimentalTime
    sealed class Timer: TimePattern() {
        /**
         * Timer expiring after the specified time.
         */
        data class ExpiringTimer(val expiration: Duration): Timer()

        /**
         * Timer expiring randomly within a time range.
         */
        data class RandomExpiringTimer(val expiration: ClosedRange<Duration>): Timer()

        /**
         * Timer that repeats.
         *
         * @param occurrences If specified, this is the number of times the timer will repeat.
         */
        data class RecurringTimer(val occurrences: Int?, val time: Duration): Timer()

        /**
         * Timer that repeats and expires randomly within a time range.
         *
         * @param occurrences If specified, this is the number of times the timer will repeat.
         */
        data class RandomRecurringTimer(val occurrences: Int?, val timeRange: ClosedRange<Duration>): Timer()
    }
}
