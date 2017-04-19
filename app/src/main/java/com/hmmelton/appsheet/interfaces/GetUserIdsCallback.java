package com.hmmelton.appsheet.interfaces;

import java.util.List;

/**
 * Created by harrison on 4/18/17.
 * This interface is a callback used for fetching user ID's from the AppSheet web service.
 */

public interface GetUserIdsCallback {
    void onComplete(List<Integer> userIds);
}
