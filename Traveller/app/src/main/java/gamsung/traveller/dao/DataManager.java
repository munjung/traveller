package gamsung.traveller.dao;

import android.content.Context;

import java.util.List;

import gamsung.traveller.model.Topic;

/**
 * Created by shin on 2018. 1. 8..
 */

public class DataManager {

    private static SQLiteHelper m_sqlHelper;
    private static DataManager m_instance;

    public static DataManager getInstance(Context context){

        if(m_instance == null)
            m_instance = new DataManager(context);

        return  m_instance;
    }

    private DataManager(Context context){

        m_sqlHelper = new SQLiteHelper(context);
    }

    /*
     * Topic
     */
    public List<Topic> getTopics(){

        return TopicManager.getTopics(m_sqlHelper);
    }

    public void deleteTopic(String name){

        TopicManager.deleteTopic(m_sqlHelper, name);
    }

    public void insertTopic(Topic topic){

        long id = TopicManager.insertTopic(m_sqlHelper, topic);
        topic.set_id(id);
    }

    public void updateTopic(Topic topic){

        TopicManager.updateTopic(m_sqlHelper, topic);
    }

    /*
     * Route
     */



    /*
     * Place
     */


    /*
     * Photograph
     */



    /*
     * SearchPlace
     */
}
