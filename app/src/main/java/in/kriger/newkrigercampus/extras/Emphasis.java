package in.kriger.newkrigercampus.extras;


public interface Emphasis {

    void setTextHighlightColor(String highlightColorHex);

    void setTextHighlightColor(int colorResource);

    void setHighlightMode(HighlightMode highlightMode);

    void setTextToHighlight(String textToHighlight);

    void setCaseInsensitive(boolean caseInsensitive);

    void highlight();
}
