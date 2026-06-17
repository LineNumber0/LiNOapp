package ma.oprojet.ln0wp.Models;

import com.google.gson.annotations.SerializedName;

public class Media {
    private int id;
    private String date;
    private Media_Rendered guid;
    private String slug;
    private String type;
    private String link;
    private Media_Rendered title;
    private int author;
    private String alt_text;
    private Media_Rendered caption;
    private Media_Rendered description;
    private String media_type;
    private String mime_type;
    private Media_Details media_details;
    private String source_url;

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

    public Media_Rendered getGuid() {
        return guid;
    }

    public void setGuid(Media_Rendered guid) {
        this.guid = guid;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Media_Rendered getTitle() {
        return title;
    }

    public void setTitle(Media_Rendered title) {
        this.title = title;
    }

    public int getAuthor() {
        return author;
    }

    public void setAuthor(int author) {
        this.author = author;
    }

    public String getAlt_text() {
        return alt_text;
    }

    public void setAlt_text(String alt_text) {
        this.alt_text = alt_text;
    }

    public Media_Rendered getCaption() {
        return caption;
    }

    public void setCaption(Media_Rendered caption) {
        this.caption = caption;
    }

    public Media_Rendered getDescription() {
        return description;
    }

    public void setDescription(Media_Rendered description) {
        this.description = description;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String getMime_type() {
        return mime_type;
    }

    public void setMime_type(String mime_type) {
        this.mime_type = mime_type;
    }

    public Media_Details getMedia_details() {
        return media_details;
    }

    public void setMedia_details(Media_Details media_details) {
        this.media_details = media_details;
    }

    public String getSource_url() {
        return source_url;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public class Media_Rendered {

        private String rendered;

        public String getRendered() {
            return rendered;
        }

        public void setRendered(String rendered) {
            this.rendered = rendered;
        }
    }

    public class Media_Details {

        private int width;
        private int height;
        private String file;
        private Media_Details_Sizes sizes;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public Media_Details_Sizes getSizes() {
            return sizes;
        }

        public void setSizes(Media_Details_Sizes sizes) {
            this.sizes = sizes;
        }
    }

    public class Media_Details_Sizes {

        private Media_Details_Sizes_Media thumbnail;
        private Media_Details_Sizes_Media medium;
        private Media_Details_Sizes_Media medium_large;
        private Media_Details_Sizes_Media large;
        private Media_Details_Sizes_Media full;

        public Media_Details_Sizes_Media getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(Media_Details_Sizes_Media thumbnail) {
            this.thumbnail = thumbnail;
        }

        public Media_Details_Sizes_Media getMedium() {
            return medium;
        }

        public void setMedium(Media_Details_Sizes_Media medium) {
            this.medium = medium;
        }

        public Media_Details_Sizes_Media getMedium_large() {
            return medium_large;
        }

        public void setMedium_large(Media_Details_Sizes_Media medium_large) {
            this.medium_large = medium_large;
        }

        public Media_Details_Sizes_Media getLarge() {
            return large;
        }

        public void setLarge(Media_Details_Sizes_Media large) {
            this.large = large;
        }

        public Media_Details_Sizes_Media getFull() {
            return full;
        }

        public void setFull(Media_Details_Sizes_Media full) {
            this.full = full;
        }
    }

    public class Media_Details_Sizes_Media {
        private String file;
        private int width;
        private int height;
        private String mime_type;
        private String source_url;

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public String getMime_type() {
            return mime_type;
        }

        public void setMime_type(String mime_type) {
            this.mime_type = mime_type;
        }

        public String getSource_url() {
            return source_url;
        }

        public void setSource_url(String source_url) {
            this.source_url = source_url;
        }
    }
}
