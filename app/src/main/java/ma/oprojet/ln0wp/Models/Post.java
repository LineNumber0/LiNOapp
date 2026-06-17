package ma.oprojet.ln0wp.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Post {
    private int id;
    private String date; // "yyyy-MM-dd'T'HH:mm:ss"
    private String slug;
    private String link;
    private String type;
    private Post_Rendered guid;
    private Post_Rendered title;
    private Post_Rendered content;
    private Post_Rendered excerpt;
    private int author;
    private int featured_media;
    private int[] categories;
    private int[] tags;
    @SerializedName("_embedded")
    private Embedded embedded;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFeaturedImage() {
        if (embedded != null && embedded.getFeaturedMedia() != null
                && !embedded.getFeaturedMedia().isEmpty()) {
            return embedded.getFeaturedMedia().get(0).getSourceUrl();
        }
        return null;
    }

    /**
     * Just get a i18n formated date
     * @param \String based_date
     * @param \Locale lang
     * @return String
     */

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Post_Rendered getGuid() {
        return guid;
    }

    public void setGuid(Post_Rendered guid) {
        this.guid = guid;
    }

    public Post_Rendered getTitle() {
        return title;
    }

    public void setTitle(Post_Rendered title) {
        this.title = title;
    }

    public Post_Rendered getContent() {
        return content;
    }

    public void setContent(Post_Rendered content) {
        this.content = content;
    }

    public Post_Rendered getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(Post_Rendered excerpt) {
        this.excerpt = excerpt;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    public int getFeatured_media() {
        return featured_media;
    }

    public void setFeatured_media(int featured_media) {
        this.featured_media = featured_media;
    }

    public int[] getCategories() {
        return categories;
    }

    public void setCategories(int[] categories) {
        this.categories = categories;
    }

    public int[] getTags() {
        return tags;
    }

    public void setTags(int[] tags) {
        this.tags = tags;
    }

    public class Post_Rendered {

        private String rendered;

        public String getRendered() {
            return rendered;
        }

        public void setRendered(String rendered) {
            this.rendered = rendered;
        }

    }

    public static class Embedded {
        @SerializedName("wp:featuredmedia")
        private List<FeaturedMedia> featuredMedia;

        public List<FeaturedMedia> getFeaturedMedia() { return featuredMedia; }
    }

    public static class FeaturedMedia {
        @SerializedName("source_url")
        private String sourceUrl;
        public String getSourceUrl() { return sourceUrl; }
    }
}