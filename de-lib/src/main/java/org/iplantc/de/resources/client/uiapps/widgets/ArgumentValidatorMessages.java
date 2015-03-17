package org.iplantc.de.resources.client.uiapps.widgets;

import com.google.gwt.i18n.client.Messages;

public interface ArgumentValidatorMessages extends Messages {

    String regex(String regex);

    String characterLimit(int limit);

    String intAbove(int above);

    String intBelow(int below);

    String intRange(int above, int below);

    String dblAbove(double above);

    String dblBelow(double below);

    String dblRange(double above, double below);

    String ruleType();

    String valueRegex();

    String valueCharLimit();

    String valueAbove();

    String valueBelow();

    String valueBetween();

    String valueBetweenAnd();

    String validatorDialogHeading();

    String regexLabel();

    String characterLimitLabel();

    String intAboveLabel();

    String intBelowLabel();

    String intRangeLabel();

    String dblAboveLabel();

    String dblBelowLabel();

    String dblRangeLabel();
}
