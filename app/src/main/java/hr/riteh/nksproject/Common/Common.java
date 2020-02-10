package hr.riteh.nksproject.Common;

import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import hr.riteh.nksproject.R;

public class Common {

    public static ArrayList<String> getTasks(ArrayList<String> tasks){

        tasks.add("FILE -> SHARE ->WHATSAPP");
        tasks.add("INSERT -> CHART");
        tasks.add("LAYOUT -> ORIENTATION");
        tasks.add("FILE  -> OPEN");
        tasks.add("INSERT -> EQUATION");
        tasks.add("TEXT -> COLOR -> GREEN");
        tasks.add("LAYOUT -> BACKGROUND");
        tasks.add("FILE -> EDIT -> DELETE");
        tasks.add("TEXT -> SIZE -> 18PT");
        tasks.add("LAYOUT -> BREAKS");
        tasks.add("FILE -> SHARE -> FACEBOOK");
        tasks.add("IMAGE -> ROTATE -> -45");

        return tasks;
    }

    public static ArrayList<String> getTasks2(ArrayList<String> tasks){

        tasks.add("FILE -> EDIT -> DELETE");//0 1 2
        tasks.add("INSERT -> CHART");// 3 4
        tasks.add("LAYOUT -> ORIENTATION"); //5 6
        tasks.add("FILE  -> OPEN");//7 8
        tasks.add("INSERT -> EQUATION");// 9 10
        tasks.add("TEXT -> COLOR -> GREEN");// 11 12 13
        tasks.add("LAYOUT -> BACKGROUND");//14 15
        tasks.add("FILE -> SHARE ->WHATSAPP");// 16 17 18
        tasks.add("TEXT -> SIZE -> 18PT");//19 20 21
        tasks.add("LAYOUT -> BREAKS");//22 23
        tasks.add("FILE -> SHARE -> FACEBOOK");// 24 25 26
        tasks.add("IMAGE -> ROTATE -> -45");//27 28 29

        return tasks;
    }


    public static ArrayList<Integer> getTasksWeight(ArrayList<Integer> tasksWeight){

        tasksWeight.add(3);
        tasksWeight.add(2);
        tasksWeight.add(2);
        tasksWeight.add(2);
        tasksWeight.add(2);
        tasksWeight.add(3);
        tasksWeight.add(2);
        tasksWeight.add(3);
        tasksWeight.add(3);
        tasksWeight.add(2);
        tasksWeight.add(3);
        tasksWeight.add(3);

        return tasksWeight;

    }

}











