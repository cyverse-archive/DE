package org.iplantc.de.client.models.search;

import org.iplantc.de.client.models.HasLabel;

import java.util.Date;

public interface DateInterval extends HasLabel {

    Date getFrom();

    Date getTo();

    void setFrom(Date from);

    void setTo(Date to);

}
