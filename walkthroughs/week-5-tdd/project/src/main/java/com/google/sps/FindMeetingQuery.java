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

  /**
   * Return a list of time periods that can schedule a request among events
   **/
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {

    for (Event event: events) {
        System.out.println(event);
    }
    System.out.println("DEBUG \n");


    Collection<TimeRange> timeRanges = new ArrayList<TimeRange>();
    for (Event event: events) {
        timeRanges.add(event.getWhen());
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

  private TimeRange getTimeWithStart(int startTime, Collection<TimeRange> timeRanges) {
      for (TimeRange timeRange: timeRanges) {
          if (timeRange.start() == startTime)
            return timeRange;
      }
      return null;
  }

  private int getStartTime(int endTime, Collection<TimeRange> timeRanges){
      // if overlap, get overlap with the latest start time
      TimeRange currentEvent = getTimeWithStart(endTime, timeRanges);

      if (currentEvent == null) {
          System.out.println("ERROR: (endtime: " + endTime + ")");
      }

        ArrayList<TimeRange> overlaps =  new ArrayList<TimeRange>();
        for (TimeRange otherEvent: timeRanges) {
            if (currentEvent != otherEvent && currentEvent.overlaps(otherEvent)) {
                // get overlap with the latest startTime
                overlaps.add(otherEvent);
            }
        }
        
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

//   private getStartTime(int i, Collection<TimeRange> timeRanges) {
//       for (TimeRange range: timeRanges) {
//           if (range.start() > i)
//             return range.start();
//       }
//   }

  /**
   * Check whether a time conflicts with a set of events 
  **/
  private boolean conflicts(TimeRange proposedTime, Collection<Event> events) {
      for (Event event: events){
          if (proposedTime.overlaps(event.getWhen()))
            return true;
      }

      return false;
  }

  private int getNextStartTime(Event currentEvent, Collection<Event> prunedEvents){
      // if overlap, get overlap with the latest start time

        ArrayList<TimeRange> overlaps =  new ArrayList<TimeRange>();
        for (Event otherEvent: prunedEvents) {
            if (currentEvent != otherEvent && currentEvent.getWhen().overlaps(otherEvent.getWhen())) {
                // get overlap with the latest startTime
                overlaps.add(otherEvent.getWhen());
            }
        }
        
        if (overlaps.size() != 0) {
            Collections.sort(overlaps, TimeRange.ORDER_BY_END);
            TimeRange nextEvent = overlaps.get(overlaps.size()-1);
            return nextEvent.end();
        } else {
            return currentEvent.getWhen().end();
        }
  }

  private boolean isValid(TimeRange period) {
      return true;
  }
}
