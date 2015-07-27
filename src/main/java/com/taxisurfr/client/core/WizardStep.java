package com.taxisurfr.client.core;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;

public interface WizardStep
{

    String getCaption();

    Composite getContent();

    Boolean onNext();

    Boolean onBack();

    void clear();

    void show(boolean visible, Button prev);

}
