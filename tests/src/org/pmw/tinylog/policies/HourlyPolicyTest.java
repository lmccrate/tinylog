/*
 * Copyright 2012 Martin Winandy
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package org.pmw.tinylog.policies;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * Tests for hourly policy.
 * 
 * @see HourlyPolicy
 */
public class HourlyPolicyTest extends AbstractTimeBasedTest {

	/**
	 * Test rolling at first full hour.
	 */
	@Test
	public final void testRollingAtFirstFullHour() {
		setTime(HOUR / 2L); // 00:30

		Policy policy = new HourlyPolicy();
		assertTrue(policy.check(null, null));
		increaseTime(HOUR / 2L); // 01:00
		assertFalse(policy.check(null, null));

		policy.reset();
		assertTrue(policy.check(null, null));
		increaseTime(HOUR / 2L); // 01:30
		assertTrue(policy.check(null, null));
		increaseTime(HOUR / 2L); // 02:00
		assertFalse(policy.check(null, null));
	}

	/**
	 * Test continuing log files.
	 * 
	 * @throws IOException
	 *             Problem with the temporary file
	 */
	@Test
	public final void testContinueLogFile() throws IOException {
		setTime(HOUR / 2L); // 00:30
		File file = File.createTempFile("test", ".tmp");
		file.deleteOnExit();
		file.setLastModified(getTime());

		Policy policy = new HourlyPolicy();
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, null));
		increaseTime(HOUR / 2L - 1L); // 01:59:59,999
		assertTrue(policy.check(null, null));
		increaseTime(1L); // 02:00
		assertFalse(policy.check(null, null));

		increaseTime(-1L); // 01:59:59,999
		policy = new HourlyPolicy();
		assertTrue(policy.initCheck(file));
		assertTrue(policy.check(null, null));
		increaseTime(1L); // 02:00
		assertFalse(policy.check(null, null));

		file.delete();

		policy = new HourlyPolicy();
		assertTrue(policy.initCheck(file));
	}

}