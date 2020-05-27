package com.example.trading.Fragments.Post;

public class WriteRCData {

    String imageUri;

    public WriteRCData(){

    }

    public WriteRCData(String uri){
        this.imageUri = uri;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
