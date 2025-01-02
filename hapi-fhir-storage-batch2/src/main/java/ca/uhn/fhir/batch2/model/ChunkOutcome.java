/*-
 * #%L
 * HAPI FHIR JPA Server - Batch2 Task Processor
 * %%
 * Copyright (C) 2014 - 2025 Smile CDR, Inc.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package ca.uhn.fhir.batch2.model;

public class ChunkOutcome {
	public enum Status {
		SUCCESS,
		FAILED;
	}

	private final Status myStatus;

	public ChunkOutcome(Status theStatus) {
		myStatus = theStatus;
	}

	public Status getStatus() {
		return myStatus;
	}

	public static ChunkOutcome SUCCESS() {
		return new ChunkOutcome(Status.SUCCESS);
	}
}
