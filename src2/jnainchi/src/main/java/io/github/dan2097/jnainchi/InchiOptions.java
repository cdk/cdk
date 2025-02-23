/**
 * JNA-InChI - Library for calling InChI from Java
 * Copyright © 2018 Daniel Lowe
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dan2097.jnainchi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public class InchiOptions {
  
  static final InchiOptions DEFAULT_OPTIONS = new InchiOptionsBuilder().build();
  
  private static final boolean IS_WINDOWS = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).startsWith("windows");

  private final List<InchiFlag> flags;
  private final long timeoutMilliSecs;

  private InchiOptions(InchiOptionsBuilder builder) {
    this.flags = Collections.unmodifiableList(new ArrayList<InchiFlag>(builder.flags));
    this.timeoutMilliSecs = builder.timeoutMilliSecs;
  }

  public static class InchiOptionsBuilder {

    private final EnumSet<InchiFlag> flags = EnumSet.noneOf(InchiFlag.class);
    private long timeoutMilliSecs = 0;

    public InchiOptionsBuilder withFlag(InchiFlag... flags) {
      for (InchiFlag flag : flags) {
        this.flags.add(flag);
      }
      return this;
    }
    
    /**
     * Timeout in seconds (0 = infinite timeout)
     * @param timeoutSecs
     * @return
     */
    public InchiOptionsBuilder withTimeout(int timeoutSecs) {
      if (timeoutSecs < 0) {
        throw new IllegalArgumentException("Timeout should be a time in seconds or 0 for infinite: " + timeoutSecs);
      }
      this.timeoutMilliSecs = (long) timeoutSecs * 1000;
      return this;
    }
    
    /**
     * Timeout in milliseconds (0 = infinite timeout)
     * @param timeoutMilliSecs
     * @return
     */
    public InchiOptionsBuilder withTimeoutMilliSeconds(long timeoutMilliSecs) {
      if (timeoutMilliSecs < 0) {
        throw new IllegalArgumentException("Timeout should be a time in milliseconds or 0 for infinite: " + timeoutMilliSecs);
      }
      this.timeoutMilliSecs = timeoutMilliSecs;
      return this;
    }

    public InchiOptions build() {
      int stereoOptionFlags = 0;
      int chiralFlagFlags = 0;
      for (InchiFlag flag : flags) {
        switch (flag) {
        case SNon:
        case SRac:
        case SRel:
        case SUCF:
        case SAbs:
          stereoOptionFlags++;
          break;
        case ChiralFlagOFF:
        case ChiralFlagON:
          chiralFlagFlags++;
          break;
        default:
          break;
        }
      }
      if (stereoOptionFlags > 1) {
        throw new IllegalArgumentException("Ambiguous flags: SAbs, SNon, SRel, SRac and SUCF are mutually exclusive");
      }
      if (chiralFlagFlags > 1) {
        throw new IllegalArgumentException("Ambiguous flags: ChiralFlagOFF and ChiralFlagON are mutually exclusive");
      }
      return new InchiOptions(this);
    }
  }
  
  public List<InchiFlag> getFlags() {
    return flags;
  }
  
  public int getTimeout() {
    return (int) (timeoutMilliSecs/1000);
  }

  public long getTimeoutMilliSeconds() {
    return timeoutMilliSecs;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (InchiFlag inchiFlag : flags) {
      if (inchiFlag == InchiFlag.SAbs) {
        continue;
      }
      if (sb.length() > 0) {
        sb.append(' ');
      }
      sb.append(IS_WINDOWS ? "/" : "-");
      sb.append(inchiFlag.toString());
    }
    if (timeoutMilliSecs != 0) {
      if (sb.length() > 0) {
        sb.append(' ');
      }
      sb.append(IS_WINDOWS ? "/" : "-");
      sb.append("WM");
      sb.append(String.valueOf(timeoutMilliSecs));
    }
    return sb.toString();
  }
}
