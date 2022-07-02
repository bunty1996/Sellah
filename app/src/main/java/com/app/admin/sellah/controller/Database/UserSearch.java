package com.app.admin.sellah.controller.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_search")
public class UserSearch
{

    @PrimaryKey(autoGenerate = true)
    int search_id;

    String searched;

    public int getSearch_id() {
        return search_id;
    }

    public void setSearch_id(int search_id) {
        this.search_id = search_id;
    }

    public String getSearched() {
        return searched;
    }

    public void setSearched(String searched) {
        this.searched = searched;
    }
}
