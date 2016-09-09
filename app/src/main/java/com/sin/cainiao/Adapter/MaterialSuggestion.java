package com.sin.cainiao.Adapter;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;


public class MaterialSuggestion implements SearchSuggestion {

    private String mFoodName;
    private boolean mIsHistory = false;

    public MaterialSuggestion(String suggestion){
        this.mFoodName = suggestion.toLowerCase();
    }

    public MaterialSuggestion(Parcel source) {
        this.mFoodName = source.readString();
        this.mIsHistory = source.readInt() != 0;
    }

    @Override
    public String getBody() {
        return mFoodName;
    }

    public void setIsHistory(boolean isHistory){
        this.mIsHistory = isHistory;
    }

    public boolean getIsHistory(){
        return this.mIsHistory;
    }

    public static final Creator<SearchSuggestion> CREATOR = new Creator<SearchSuggestion>() {
        @Override
        public SearchSuggestion createFromParcel(Parcel source) {
            return new MaterialSuggestion(source);
        }

        @Override
        public SearchSuggestion[] newArray(int size) {
            return new SearchSuggestion[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFoodName);
        dest.writeInt(mIsHistory ? 1 : 0);
    }
}
