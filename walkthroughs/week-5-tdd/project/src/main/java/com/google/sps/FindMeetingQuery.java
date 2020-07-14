// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import com.google.sps.Event;
import java.util.Collections;

public final class FindMeetingQuery {

  /** Return a collection of time periods where all attendees in `request` are free
   * @param events - a collection of events to be scheduled for that day 
   * @param request - the details of a meeting proposal, including the attendees of 
                      that event
   * @return a collection of times where all attendees are free
   **/
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    
    // Print out input for debugging
    for (Event event: events) {
        System.out.println(event);
    }
    System.out.println("DEBUG \n");

    // Add events scheduled with mandatory and optional guests
    // Assuming mandatory guests can't be scheduled for 2 events
    Collection<TimeRange> timeRanges = new ArrayList<TimeRange>();
    for (Event event: events) {
        if (request.getAttendees().containsAll(event.getAttendees())) { // check event attendees are a subset of request attendees
            timeRanges.add(event.getWhen());
        }
    }

    // special case - event schedule at beginning for day
    TimeRange eventAtStartOfDay = eventAtStartOfDay(timeRanges);
    int startTime = (eventAtStartOfDay != null) ? eventAtStartOfDay.end() : TimeRange.START_OF_DAY;
    
    Collection<TimeRange> freePeriods = new ArrayList<TimeRange>();
    while (startTime < TimeRange.END_OF_DAY) {
        int endTime = getEndTime(startTime, timeRanges); // find upper limit of free time
        // add to free times
        TimeRange proposedTime = TimeRange.fromStartEnd(startTime, endTime, true);
        freePeriods.add(proposedTime);
        System.out.println("Proposed time: " + proposedTime);
        
        if (endTime >= TimeRange.END_OF_DAY)
            break;

        startTime = getStartTime(endTime+1, timeRanges);   // find next start time
    }

    // check if free periods are long enough
    Collection viableFreePeriods = new ArrayList<TimeRange>();
    for (TimeRange tr: freePeriods) {
        if (tr.duration() >= request.getDuration()) {
            viableFreePeriods.add(tr);
        }
    }

    return viableFreePeriods;
  }

  /** Find an event that happens at the start of the day if one exists
   * @param timeRanges - the time ranges of all the event of the days
   * @return the event at the start of the day if it exists, null otherwise
 **/ 
  private TimeRange eventAtStartOfDay(Collection<TimeRange> timeRanges) {
        for (TimeRange tr: timeRanges) {
            if (tr.start() == TimeRange.START_OF_DAY) {
                return tr;
            }
        }
        return null;
  }
  
  private int getEndTime(int i, Collection<TimeRange> timeRanges) {
      for (TimeRange range: timeRanges) {
          if (range.start() > i)
            return range.start()-1;
      }
      return TimeRange.END_OF_DAY;
  }

  /** Find the next time all attendees have a "free period" in their schedules
   * @param endTime - the ending time of the last scheduled event
   * @param timeRanges - the time ranges of all the event of the days 
   * @return the next time where are attendees are free 
  **/
  private int getStartTime(int endTime, Collection<TimeRange> timeRanges){
      // if overlap, get overlap with the latest start time
      TimeRange currentEvent = getTimeWithStart(endTime, timeRanges);

      if (currentEvent == null) {
          System.out.println("ERROR: (endtime: " + endTime + ")");
      }

        ArrayList<TimeRange> overlaps =  new ArrayList<TimeRange>();
        getOverlaps(currentEvent, timeRanges, overlaps);
        
        if (overlaps.size() != 0) {
            Collections.sort(overlaps, TimeRange.ORDER_BY_END);
            TimeRange nextEvent = overlaps.get(overlaps.size()-1);  // get latest event
            if (nextEvent.end() > currentEvent.end()) {    // check if overlap is ahead
                return nextEvent.end();
            } else {
                return currentEvent.end();
            }
        } else {
            return currentEvent.end();  // if there's no overlap, just start at end of current event
        }
  }

  /** Find an event with the start time of `startTime`
   * @param startTime - the start time of the event to find
   * @param timeRanges - the times of all events 
   * @return - the event that starts at `startTime` or `null` if none exists
  **/ 
  private TimeRange getTimeWithStart(int startTime, Collection<TimeRange> timeRanges) {
      for (TimeRange timeRange: timeRanges) {
          if (timeRange.start() == startTime)
            return timeRange;
      }
      return null;
  }

  private void getOverlaps(TimeRange currentEvent, Collection<TimeRange> timeRanges, Collection<TimeRange> overlaps) {
        for (TimeRange otherEvent: timeRanges) {
            if (currentEvent.overlaps(otherEvent) && !overlaps.contains(otherEvent)) {
                // get overlap with the latest startTime
                overlaps.add(otherEvent);
                getOverlaps(otherEvent, timeRanges, overlaps);
            }
        }
  }

}
