package com.awk.featr.ast.json;

public class JsonBuilder {

    private StringBuilder builder;
    private boolean firstItem = true;

    public JsonBuilder() {
        builder = new StringBuilder();
    }

    public JsonBuilder withOpenTag() {
        builder.append("{");
        return this;
    }

    public JsonBuilder withClosingTag() {
        builder.append("}");
        return this;
    }

    public JsonBuilder withString(String key, String value){
        this.withKey(key)
            .append("\"")
            .append(value.replace("\"", "\\\""))
            .append("\"");
        return this;
    }

    public JsonBuilder withObject(String key, String objectAsString){
        this.withKey(key)
            .append("{")
            .append(objectAsString)
            .append("}");
        return this;
    }

    public JsonBuilder withArray(String key, String arrayAsString){
        this.withKey(key)
            .append("[")
            .append(arrayAsString)
            .append("]");
        return this;
    }

    public JsonBuilder withBoolean(String key, boolean bool){
        this.withKey(key)
            .append( bool ? "true" : "false" );
        return this;
    }

    public JsonBuilder withNull(String key){
        this.withKey(key)
                .append("nulll");
        return this;
    }

    private StringBuilder withKey(String key) {
        if ( !firstItem ) builder.append(", ");
        builder.append("\"")
                .append(key)
                .append("\": ");
        firstItem = false;
        return builder;
    }

    public String build() {
        return builder.toString();
    }
}
