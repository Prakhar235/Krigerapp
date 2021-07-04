package in.kriger.newkrigercampus.classes;

public class PreviewPost {
    String name, headline, imageurl;
    private String count_comment,count_like,count_views;
    private String text,image_url;

    public PreviewPost(String name,String headline, String imageurl,String text,String count_views,String count_like,String count_comment) {

        this.name = name;
        this.headline = headline;
        this.imageurl = imageurl;
        this.text  = text;
        this.count_views = count_views;
        this.count_like = count_like;
        this.count_comment = count_comment;

    }

    public PreviewPost() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
    public String getCount_views() {
        return count_views;
    }

    public void setCount_views(String count_views) {
        this.count_views = count_views;
    }


    public String  getCount_comment() {
        return count_comment;
    }

    public void setCount_comment(String count_comment) {
        this.count_comment = count_comment;
    }

    public String getCount_like() {
        return count_like;
    }

    public void setCount_like(String count_like) {
        this.count_like = count_like;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }


}
