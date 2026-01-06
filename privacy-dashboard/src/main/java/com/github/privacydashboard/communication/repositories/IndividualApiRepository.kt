package com.github.privacydashboard.communication.repositories

import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.models.v2.individual.Individual
import com.github.privacydashboard.models.v2.individual.IndividualRequest

class IndividualApiRepository(private val apiService: PrivacyDashboardAPIServices) {

    suspend fun createAnIndividual(
        name: String?,
        email: String?,
        phone: String?,
        pushNotificationToken: String?,
        deviceType: String?,
        externalId: String?
    ): Result<IndividualRequest?>? {
        return try {
            val individual = IndividualRequest(
                individual = Individual(
                    name = name,
                    email = email,
                    phone = phone,
                    pushNotificationToken = pushNotificationToken,
                    deviceType = deviceType,
                    externalId = externalId
                )
            )
            val response = apiService.createAnIndividual(
                individual
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readTheIndividual(
        individualId: String?
    ): Result<IndividualRequest?>? {
        return try {
            val response = apiService.readAnIndividual(
                individualId = individualId
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateIndividual(
        individualId: String?,
        name: String,
        email: String,
        phone: String,
        pushNotificationToken: String?,
        deviceType: String?,
        externalId: String?
    ): Result<IndividualRequest?>? {
        return try {
            val individual = IndividualRequest(
                individual = Individual(
                    name = name,
                    email = email,
                    phone = phone,
                    pushNotificationToken = pushNotificationToken,
                    deviceType = deviceType,
                    externalId = externalId
                )
            )
            val response = apiService.updateAnIndividual(
                individualId = individualId,
                body = individual
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllIndividuals(
        offset:Int?,
        limit:Int?,
        externalIndividualId: String?
    ): Result<IndividualRequest?>? {
        return try {
            val response = apiService.getAllIndividual(
                offset = offset,
                limit = limit,
                externalIndividualId = externalIndividualId
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteTheIndividual(
        individualId: String?
    ): Result<IndividualRequest?>? {
        return try {
            val response = apiService.deleteAnIndividual(
                individualId = individualId
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}