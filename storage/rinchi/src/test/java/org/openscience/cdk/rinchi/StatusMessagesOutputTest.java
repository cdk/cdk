/* Copyright (C) 2024 Uli Fechner
 *
 * Contact: cdk-devel@lists.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.openscience.cdk.rinchi;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Uli Fechner
 */
class StatusMessagesOutputTest {

    static Stream<Arguments> statusGetHigherSeverityTestMethodSource() {
        return Stream.of(
                Arguments.of(StatusMessagesOutput.Status.SUCCESS, StatusMessagesOutput.Status.SUCCESS, StatusMessagesOutput.Status.SUCCESS),
                Arguments.of(StatusMessagesOutput.Status.SUCCESS, StatusMessagesOutput.Status.WARNING, StatusMessagesOutput.Status.WARNING),
                Arguments.of(StatusMessagesOutput.Status.SUCCESS, StatusMessagesOutput.Status.ERROR, StatusMessagesOutput.Status.ERROR),
                Arguments.of(StatusMessagesOutput.Status.WARNING, StatusMessagesOutput.Status.SUCCESS, StatusMessagesOutput.Status.WARNING),
                Arguments.of(StatusMessagesOutput.Status.WARNING, StatusMessagesOutput.Status.WARNING, StatusMessagesOutput.Status.WARNING),
                Arguments.of(StatusMessagesOutput.Status.WARNING, StatusMessagesOutput.Status.ERROR, StatusMessagesOutput.Status.ERROR),
                Arguments.of(StatusMessagesOutput.Status.ERROR, StatusMessagesOutput.Status.SUCCESS, StatusMessagesOutput.Status.ERROR),
                Arguments.of(StatusMessagesOutput.Status.ERROR, StatusMessagesOutput.Status.WARNING, StatusMessagesOutput.Status.ERROR),
                Arguments.of(StatusMessagesOutput.Status.ERROR, StatusMessagesOutput.Status.ERROR, StatusMessagesOutput.Status.ERROR)
        );
    }

    @ParameterizedTest
    @MethodSource("statusGetHigherSeverityTestMethodSource")
    void statusGetHigherSeverityTest(final StatusMessagesOutput.Status statusThis, final StatusMessagesOutput.Status statusOther, final StatusMessagesOutput.Status expected) {
        assertEquals(expected, statusThis.getHigherSeverity(statusOther));
    }
}