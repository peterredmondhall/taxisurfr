package com.taxisurfr.client.steps.ui.widget;

import java.util.List;

import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.taxisurfr.client.core.Wizard;
import com.taxisurfr.shared.model.RatingInfo;

public class RatingList
{
    List<RatingInfo> ratings;

    public RatingList(List<RatingInfo> ratings)
    {
        this.ratings = ratings;
    }

    /**
     * Create a form that contains undisclosed advanced options.
     */
    public Widget createRatingForm()
    {
        DecoratorPanel decPanel = new DecoratorPanel();
        decPanel.add(getDisclosure(getLayout()));
        return decPanel;
    }

    private FlexTable getLayout()
    {
        FlexTable layout = new FlexTable();
        layout.setCellSpacing(6);
        layout.setWidth("100%");
        FlexCellFormatter cellFormatter = layout.getFlexCellFormatter();

        // Add a title to the form
        cellFormatter.setColSpan(0, 0, 3);
        cellFormatter.setHorizontalAlignment(
                0, 0, HasHorizontalAlignment.ALIGN_CENTER);

        int index = 1;
        for (RatingInfo ratingInfo : ratings)
        {
            layout.setWidget(index, 0, getStars(ratingInfo.getAverage()));
            layout.setWidget(index++, 1, getDisclosure(ratingInfo));

        }
        return layout;
    }

    private Widget getDisclosure(FlexTable layout)
    {
        DisclosurePanel advancedDisclosure = new DisclosurePanel("Ratings from our customers.");
        advancedDisclosure.setAnimationEnabled(true);
        advancedDisclosure.setContent(layout);

        return advancedDisclosure;

    }

    private Widget getDisclosure(RatingInfo ratingInfo)
    {

        Grid maingrid = new Grid(1, 2);

        Grid grid = new Grid(5, 2);
        int i = 0;
        grid.setHTML(i, 0, "cleanliness");
        grid.setWidget(i++, 1, getStars(ratingInfo.getCleanliness()));
        grid.setHTML(i, 0, "safety");
        grid.setWidget(i++, 1, getStars(ratingInfo.getSafety()));
        grid.setHTML(i, 0, "punctuality");
        grid.setWidget(i++, 1, getStars(ratingInfo.getPunctuality()));
        grid.setHTML(i, 0, "professionality");
        grid.setWidget(i++, 1, getStars(ratingInfo.getProfessionality()));
        grid.setHTML(i, 0, ratingInfo.getAuthor());

        maingrid.setWidget(0, 0, grid);
        if (!Wizard.MOBILE)
        {
            maingrid.setWidget(0, 1, new Label(ratingInfo.getCritic()));
        }

        // CellFormatter cellFormatter = grid.getCellFormatter();

        int max = ratingInfo.getCritic().length() > 30 ? 30 : ratingInfo.getCritic().length();
        DisclosurePanel advancedDisclosure = new DisclosurePanel(
                ratingInfo.getCritic().substring(0, max));
        advancedDisclosure.setAnimationEnabled(true);
        advancedDisclosure.ensureDebugId("criticDisclosure");
        advancedDisclosure.setContent(maingrid);

        return advancedDisclosure;
    }

    private Image getStars(int value)
    {
        String src = "images/" + value + "stars.png";
        Image img = new Image(src);
        img.setHeight("20px");
        return img;

    }
}