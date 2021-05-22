package com.byted.camp.todolist.db;

import android.provider.BaseColumns;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量 !done
    private TodoContract() {
    }

    public static class TodoEntry implements BaseColumns{
        public static final String TABLE_NAME = "note";
        public static final String COLUMN_1 = "id";
        public static final String COLUMN_2 = "date";
        public static final String COLUMN_3 = "state";
        public static final String COLUMN_4 = "content";
        public static final String COLUMN_5 = "priority";
    }

}
