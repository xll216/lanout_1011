package com.lanou.lilyxiao.myapplication.bean;

import java.util.List;

/**
 * 　　　　　　　　┏┓　　　┏┓+ +
 * 　　　　　　　┏┛┻━━━┛┻┓ + +
 * 　　　　　　　┃　　　　　　　┃
 * 　　　　　　　┃　　　━　　　┃ ++ + + +
 * 　　　　　　 ████━████ ┃+
 * 　　　　　　　┃　　　　　　　┃ +
 * 　　　　　　　┃　　　┻　　　┃
 * 　　　　　　　┃　　　　　　　┃ + +
 * 　　　　　　　┗━┓　　　┏━┛
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃ + + + +
 * 　　　　　　　　　┃　　　┃　　　　Code is far away from bug with the animal protecting
 * 　　　　　　　　　┃　　　┃ + 　　　　神兽保佑,代码无bug
 * 　　　　　　　　　┃　　　┃
 * 　　　　　　　　　┃　　　┃　　+
 * 　　　　　　　　　┃　 　　┗━━━┓ + +
 * 　　　　　　　　　┃ 　　　　　　　┣┓
 * 　　　　　　　　　┃ 　　　　　　　┏┛
 * 　　　　　　　　　┗┓┓┏━┳┓┏┛ + + + +
 * 　　　　　　　　　　┃┫┫　┃┫┫
 * 　　　　　　　　　　┗┻┛　┗┻┛+ + + +
 */
public class LatestEntiry {


    /**
     * date : 20161227
     * stories : [{"images":["http://pic4.zhimg.com/3b4ea59aeb28e095de42d209a6303823.jpg"],"type":0,"id":9098533,"ga_prefix":"122711","title":"美元加息会导致中国经济危机吗？"},{"images":["http://pic4.zhimg.com/ad3141ccc53e68e9b1ea51c866ea5873.jpg"],"type":0,"id":9087735,"ga_prefix":"122710","title":"艺术的本质是什么，艺术家自己也不知道啊"},{"images":["http://pic2.zhimg.com/4bae3b328a58daa7eaa66c1f90797085.jpg"],"type":0,"id":9099486,"ga_prefix":"122709","title":"有哪些学术界都搞错了，忽然间有人发现问题所在的事例？"},{"images":["http://pic1.zhimg.com/134292fc43f762a54156fdb337e8d2e0.jpg"],"type":0,"id":9098778,"ga_prefix":"122708","title":"1996 年，我终于第一次在现场看 NBA"},{"title":"荤素兼备特别下饭的酸汤肥牛，十分钟就搞定","ga_prefix":"122707","images":["http://pic1.zhimg.com/21aeb415093e64305edadfeec7256704.jpg"],"multipic":true,"type":0,"id":9087858},{"title":"昨夜星辰：致敬台湾综艺 50 年","ga_prefix":"122707","images":["http://pic3.zhimg.com/a3752dc2ea9232ec3746ac0789605b62.jpg"],"multipic":true,"type":0,"id":9098816},{"images":["http://pic1.zhimg.com/8036adb6ede864b7b3e2410319898bd0.jpg"],"type":0,"id":9099458,"ga_prefix":"122707","title":"2016 年度盘点 · 安利一波好看的英剧 / 美剧"},{"images":["http://pic1.zhimg.com/8acafa463ecabb00aaf053a102a982f0.jpg"],"type":0,"id":9099354,"ga_prefix":"122706","title":"瞎扯 · 如何正确地吐槽"}]
     * top_stories : [{"image":"http://pic1.zhimg.com/b8093c6d30e638c74aa2f4913f690a3c.jpg","type":0,"id":9099458,"ga_prefix":"122707","title":"2016 年度盘点 · 安利一波好看的英剧 / 美剧"},{"image":"http://pic3.zhimg.com/2e733aab8cc07f279483affa663c9c76.jpg","type":0,"id":9098816,"ga_prefix":"122707","title":"昨夜星辰：致敬台湾综艺 50 年"},{"image":"http://pic4.zhimg.com/b6e312668b898cc9d324b65fe3fc4857.jpg","type":0,"id":9098294,"ga_prefix":"122617","title":"知乎好问题 · 为什么明明不饿，还是想吃？"},{"image":"http://pic3.zhimg.com/a59289fcd48c67a24294db62ffece6fa.jpg","type":0,"id":9097926,"ga_prefix":"122615","title":"2017 年，投资投什么，创业创什么？"},{"image":"http://pic1.zhimg.com/87c3349627685831f5601c1996802798.jpg","type":0,"id":9097680,"ga_prefix":"122614","title":"动画片里小动物的毛发是一根一根画出来的吗？"}]
     */

    private String date;
    private List<StoriesBean> stories;
    private List<TopStoriesBean> top_stories;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<StoriesBean> getStories() {
        return stories;
    }

    public void setStories(List<StoriesBean> stories) {
        this.stories = stories;
    }

    public List<TopStoriesBean> getTop_stories() {
        return top_stories;
    }

    public void setTop_stories(List<TopStoriesBean> top_stories) {
        this.top_stories = top_stories;
    }

    public static class StoriesBean {
        /**
         * images : ["http://pic4.zhimg.com/3b4ea59aeb28e095de42d209a6303823.jpg"]
         * type : 0
         * id : 9098533
         * ga_prefix : 122711
         * title : 美元加息会导致中国经济危机吗？
         * multipic : true
         */

        private int type;
        private int id;
        private String ga_prefix;
        private String title;
        private boolean multipic;
        private List<String> images;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(String ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public boolean isMultipic() {
            return multipic;
        }

        public void setMultipic(boolean multipic) {
            this.multipic = multipic;
        }

        public List<String> getImages() {
            return images;
        }

        public void setImages(List<String> images) {
            this.images = images;
        }
    }

    public static class TopStoriesBean {
        /**
         * image : http://pic1.zhimg.com/b8093c6d30e638c74aa2f4913f690a3c.jpg
         * type : 0
         * id : 9099458
         * ga_prefix : 122707
         * title : 2016 年度盘点 · 安利一波好看的英剧 / 美剧
         */

        private String image;
        private int type;
        private int id;
        private String ga_prefix;
        private String title;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getGa_prefix() {
            return ga_prefix;
        }

        public void setGa_prefix(String ga_prefix) {
            this.ga_prefix = ga_prefix;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
