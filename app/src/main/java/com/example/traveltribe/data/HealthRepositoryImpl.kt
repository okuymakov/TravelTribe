package com.example.traveltribe.data

import android.content.Context
import android.os.Build
import android.os.RemoteException
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.time.LocalDateTime

class HealthRepositoryImpl(
    private val context: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun readSleepSessions(
        from: LocalDateTime,
        until: LocalDateTime
    ): Result<List<SleepSession>> = withContext(dispatcher) {

        val sessions = mutableListOf<SleepSession>()

        val sleepSessionRequest = ReadRecordsRequest(
            recordType = StepsRecord::class,
            timeRangeFilter = TimeRangeFilter.between(
                from.minusHours(12),
                until
            ),
            ascendingOrder = false
        )

        try {
            val sleepSessions = healthConnectClient.readRecords(sleepSessionRequest)

            sleepSessions.records.forEach { session ->
                val sessionTimeFilter = TimeRangeFilter.between(session.startTime, session.endTime)
                val durationAggregateRequest = AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = sessionTimeFilter
                )
                val aggregateResponse = healthConnectClient.aggregate(durationAggregateRequest)

                val stagesRequest = ReadRecordsRequest(
                    recordType = SleepStageRecord::class,
                    timeRangeFilter = sessionTimeFilter
                )
                val stagesResponse = healthConnectClient.readRecords(stagesRequest)
                sessions.add(
                    SleepSession(
                        uid = session.metadata.id,
                        title = session.title,
                        notes = session.notes,
                        startTime = session.startTime,
                        startZoneOffset = session.startZoneOffset,
                        endTime = session.endTime,
                        endZoneOffset = session.endZoneOffset,
                        duration = aggregateResponse[SleepSessionRecord.SLEEP_DURATION_TOTAL],
                        stages = stagesResponse.records
                    )
                )
            }
            Result.success(sessions)
        } catch (exception: RemoteException) {
            Result.failure(exception)
        } catch (exception: SecurityException) {
            Result.failure(exception)
        } catch (exception: IOException) {
            Result.failure(exception)
        } catch (exception: IllegalStateException) {
            Result.failure(exception)
        }
    }
}