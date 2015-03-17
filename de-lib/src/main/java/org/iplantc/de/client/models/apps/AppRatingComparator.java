package org.iplantc.de.client.models.apps;

import java.util.Comparator;

public class AppRatingComparator implements Comparator<App> {

    @Override
    public int compare(App lhs, App rhs) {
        AppFeedback lhsFeedback = lhs.getRating();
        AppFeedback rhsFeedback = rhs.getRating();

        if (lhsFeedback == null && rhsFeedback == null) {
            return 0;
        }
        if (lhsFeedback == null) {
            return -1;
        }
        if (rhsFeedback == null) {
            return 1;
        }

        double lhsRating = lhsFeedback.getAverageRating();
        double rhsRating = rhsFeedback.getAverageRating();

        if (lhsFeedback.getUserRating() > 0) {
            lhsRating = lhsFeedback.getUserRating();
        }
        if (rhsFeedback.getUserRating() > 0) {
            rhsRating = rhsFeedback.getUserRating();
        }

        return Double.compare(lhsRating, rhsRating);
    }
}
