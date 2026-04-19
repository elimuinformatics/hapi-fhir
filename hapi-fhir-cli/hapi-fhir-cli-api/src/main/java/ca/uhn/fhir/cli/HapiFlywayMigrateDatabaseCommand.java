/*-
 * #%L
 * HAPI FHIR - Command Line Client - API
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
package ca.uhn.fhir.cli;

import ca.uhn.fhir.i18n.Msg;
import ca.uhn.fhir.jpa.migrate.HapiMigrator;
import ca.uhn.fhir.jpa.migrate.MigrationTaskList;
import ca.uhn.fhir.jpa.migrate.SchemaMigrator;
import ca.uhn.fhir.jpa.migrate.tasks.HapiFhirJpaMigrationTasks;
import ca.uhn.fhir.util.VersionEnum;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class HapiFlywayMigrateDatabaseCommand extends BaseFlywayMigrateDatabaseCommand<VersionEnum> {

	@Override
	protected List<VersionEnum> provideAllowedVersions() {
		return Arrays.asList(VersionEnum.values());
	}

	@Override
	protected Class<VersionEnum> provideVersionEnumType() {
		return VersionEnum.class;
	}

	@Override
	protected void addTasks(
			HapiMigrator theMigrator, String theSkipVersions, String theStartFromVersion, String theUpToVersion) {
		HapiFhirJpaMigrationTasks migrationTasks = new HapiFhirJpaMigrationTasks(getFlags());

		MigrationTaskList taskList;
		if (StringUtils.isNotBlank(theStartFromVersion) || StringUtils.isNotBlank(theUpToVersion)) {
			VersionEnum from = resolveVersionEnum(theStartFromVersion, VersionEnum.values()[0], START_FROM_VERSION);
			VersionEnum to = resolveVersionEnum(theUpToVersion, VersionEnum.latestVersion(), UP_TO_VERSION);

			// Validate that start-from-version is before or equal to up-to-version
			if (from.ordinal() > to.ordinal()) {
				throw new IllegalArgumentException(Msg.code(2776) + "--start-from-version (" + from
						+ ") must be before or equal to --up-to-version (" + to + ")");
			}

			taskList = migrationTasks.getTaskList(from, to);
		} else {
			taskList = migrationTasks.getAllTasks(VersionEnum.values());
		}

		taskList.setDoNothingOnSkippedTasks(theSkipVersions);
		theMigrator.addTasks(taskList);
	}

	private VersionEnum resolveVersionEnum(String theVersionString, VersionEnum theDefault, String theOptionName) {
		if (StringUtils.isBlank(theVersionString)) {
			return theDefault;
		}
		try {
			return VersionEnum.valueOf(theVersionString.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(Msg.code(2775)
					+ "Invalid value for --" + theOptionName + ": \"" + theVersionString
					+ "\". Must be a valid VersionEnum name such as V7_4_0. Valid values are: "
					+ Arrays.toString(VersionEnum.values()));
		}
	}

	@Override
	public void run(CommandLine theCommandLine) throws ParseException {
		setMigrationTableName(SchemaMigrator.HAPI_FHIR_MIGRATION_TABLENAME);
		super.run(theCommandLine);
	}
}
