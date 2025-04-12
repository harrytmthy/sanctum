/*
 * Copyright 2025 Harry Timothy Tumalewa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.harry.sanctum.core.common

import kotlinx.serialization.Serializable

@Serializable
data class StructuredQueryRequest(val structuredQuery: StructuredQuery)

@Serializable
data class StructuredQuery(
    val from: List<CollectionSelector>,
    val orderBy: List<Order>,
    val offset: Int,
    val limit: Int,
    val startAt: Cursor,
)

@Serializable
data class CollectionSelector(val collectionId: String)

@Serializable
data class Order(val field: FieldReference, val direction: String)

@Serializable
data class FieldReference(val fieldPath: String)

@Serializable
data class Cursor(val values: List<StringValue>, val before: Boolean)

@Serializable
data class StringValue(val stringValue: String)
