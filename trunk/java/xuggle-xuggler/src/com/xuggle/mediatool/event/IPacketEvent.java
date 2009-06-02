/*
 * Copyright (c) 2008, 2009 by Xuggle Incorporated.  All rights reserved.
 * 
 * This file is part of Xuggler.
 * 
 * You can redistribute Xuggler and/or modify it under the terms of the GNU
 * Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * Xuggler is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero General Public
 * License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with Xuggler.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.xuggle.mediatool.event;

import com.xuggle.mediatool.IMediaCoder;
import com.xuggle.xuggler.IPacket;

/**
 * An {@link ICoderEvent} and {@link IStreamEvent} that contains
 * an {@link IPacket} object.
 * 
 * @author aclarke
 *
 */
public interface IPacketEvent extends IStreamEvent, ICoderEvent
{

  /**
   * Returns the {@link IPacket} for this event.
   * 
   * <p>
   * The returned {@link IPacket} will only be valid for
   * the duration of the {@link com.xuggle.mediatool.IMediaListener} method call
   * it was dispatched on and 
   * implementations must not access it after
   * that call returns.  If you need to keep a copy of this data
   * either copy the data into your own object, or
   * use {@link IPacket#copyReference()} to create a reference
   * that will outlive your call.
   * </p>
   * 
   * @return the packet
   */
  public abstract IPacket getPacket();

  /**
   * {@inheritDoc}
   */
  public abstract IMediaCoder getSource();

}
