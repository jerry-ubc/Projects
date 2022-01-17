package cpen221.mp3.wikimediator;

import cpen221.mp3.fsftbuffer.Bufferable;

import java.util.NoSuchElementException;

public class Website implements Bufferable {
    //This class stores text from Wikipedia pages to avoid searching Wikipedia unnecessarily
    private final String id;
    private String pageText;

    /*
    Representation Invariant:
    id, pageText != null
     */

    /*
    Abstraction Function:
    Represents a Wikipedia page with text and page title stored.
     */

    /**
     * Basic constructor inputted with an ID to distinguish between other Websites
     * @param id Page title of the Wikipedia article
     */
    public Website(String id) {
        this.id = id;
    }

    /**
     * Sets the pageText to the parameter.
     * @param text Text given to the website to store
     */
    public void addPageText(String text) {
        pageText = text;
    }

    /**
     * Gives the pageText String.
     * @return Wikipedia article text
     */
    public String getText() throws NoSuchElementException {
        if (pageText != null) {
            return pageText;
        }
        throw new NoSuchElementException();
    }

    /**
     * Gives the Website ID.
     * @return Wikipedia page title
     */
    @Override
    public String id() {
        return id;
    }

    /**
     * @param w Other Website to compare
     * @return True if other Website has the same page title as current one
     */
    public boolean equals(Website w) {
        return w.id().equals(id);
    }
    //TODO: add hashcode()?
}
