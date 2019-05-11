package inkapplications.shade.groups

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import inkapplications.shade.auth.TokenStorage
import inkapplications.shade.config.ShadeConfig
import inkapplications.shade.serialization.CoordinatesListDeserializer
import inkapplications.shade.serialization.adapter.ShadeDeferredCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Constructs Groups services.
 */
class ShadeGroupsModule {
    /**
     * Create new instance of the Groups services.
     *
     * @param client Client to create hue requests with
     * @param config App-wide configuration for Shade, used to set up connections.
     * @param tokenStorage A place to read/write the auth token used for requests.
     */
    fun createGroups(client: OkHttpClient, config: ShadeConfig, tokenStorage: TokenStorage): ShadeGroups {
        val moshi = Moshi.Builder()
            .add(
                PolymorphicJsonAdapterFactory.of(Group::class.java, "type")
                    .withSubtype(Group.Room::class.java, "Room")
                    .withSubtype(Group.Luminaire::class.java, "Luminaire")
                    .withSubtype(Group.Lightsource::class.java, "Lightsource")
                    .withSubtype(Group.LightGroup::class.java, "LightGroup")
                    .withSubtype(Group.Entertainment::class.java, "Entertainment")
                    .withSubtype(Group.Zone::class.java, "Zone")
            )
            .add(CoordinatesListDeserializer)
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(config.baseUrl)
            .addCallAdapterFactory(ShadeDeferredCallAdapterFactory)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        val api = retrofit.create(HueGroupsApi::class.java)

        return ApiGroups(api, tokenStorage)
    }
}
