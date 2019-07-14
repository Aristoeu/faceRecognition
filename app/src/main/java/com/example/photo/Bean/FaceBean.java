package com.example.photo.Bean;


import java.util.List;

public class FaceBean {

    /**
     * image_id : FTYbZ1d4gC75KdeXEerULQ==
     * request_id : 1562721940,4e3fd05f-1a20-4de4-9023-747d0c63e935
     * time_used : 974
     * faces : [{"attributes":{"emotion":{"sadness":0.002,"neutral":0.142,"disgust":0.001,"anger":0.001,"surprise":0.027,"fear":0.001,"happiness":99.825},"gender":{"value":"Female"},"age":{"value":24},"beauty":{"female_score":83.954,"male_score":79.424},"ethnicity":{"value":"ASIAN"}},"face_rectangle":{"width":1024,"top":438,"left":311,"height":1024},"face_token":"345140039bb34247cc4a7f967240992f"}]
     */

    private String image_id;
    private String request_id;
    private int time_used;
    private List<FacesBean> faces;

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public int getTime_used() {
        return time_used;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }

    public List<FacesBean> getFaces() {
        return faces;
    }

    public void setFaces(List<FacesBean> faces) {
        this.faces = faces;
    }

    public static class FacesBean {
        /**
         * attributes : {"emotion":{"sadness":0.002,"neutral":0.142,"disgust":0.001,"anger":0.001,"surprise":0.027,"fear":0.001,"happiness":99.825},"gender":{"value":"Female"},"age":{"value":24},"beauty":{"female_score":83.954,"male_score":79.424},"ethnicity":{"value":"ASIAN"}}
         * face_rectangle : {"width":1024,"top":438,"left":311,"height":1024}
         * face_token : 345140039bb34247cc4a7f967240992f
         */

        private AttributesBean attributes;
        private FaceRectangleBean face_rectangle;
        private String face_token;

        public AttributesBean getAttributes() {
            return attributes;
        }

        public void setAttributes(AttributesBean attributes) {
            this.attributes = attributes;
        }

        public FaceRectangleBean getFace_rectangle() {
            return face_rectangle;
        }

        public void setFace_rectangle(FaceRectangleBean face_rectangle) {
            this.face_rectangle = face_rectangle;
        }

        public String getFace_token() {
            return face_token;
        }

        public void setFace_token(String face_token) {
            this.face_token = face_token;
        }

        public static class AttributesBean {
            /**
             * emotion : {"sadness":0.002,"neutral":0.142,"disgust":0.001,"anger":0.001,"surprise":0.027,"fear":0.001,"happiness":99.825}
             * gender : {"value":"Female"}
             * age : {"value":24}
             * beauty : {"female_score":83.954,"male_score":79.424}
             * ethnicity : {"value":"ASIAN"}
             */

            private EmotionBean emotion;
            private GenderBean gender;
            private AgeBean age;
            private BeautyBean beauty;
            private EthnicityBean ethnicity;

            public EmotionBean getEmotion() {
                return emotion;
            }

            public void setEmotion(EmotionBean emotion) {
                this.emotion = emotion;
            }

            public GenderBean getGender() {
                return gender;
            }

            public void setGender(GenderBean gender) {
                this.gender = gender;
            }

            public AgeBean getAge() {
                return age;
            }

            public void setAge(AgeBean age) {
                this.age = age;
            }

            public BeautyBean getBeauty() {
                return beauty;
            }

            public void setBeauty(BeautyBean beauty) {
                this.beauty = beauty;
            }

            public EthnicityBean getEthnicity() {
                return ethnicity;
            }

            public void setEthnicity(EthnicityBean ethnicity) {
                this.ethnicity = ethnicity;
            }

            public static class EmotionBean {
                /**
                 * sadness : 0.002
                 * neutral : 0.142
                 * disgust : 0.001
                 * anger : 0.001
                 * surprise : 0.027
                 * fear : 0.001
                 * happiness : 99.825
                 */

                private double sadness;
                private double neutral;
                private double disgust;
                private double anger;
                private double surprise;
                private double fear;
                private double happiness;

                public double getSadness() {
                    return sadness;
                }

                public void setSadness(double sadness) {
                    this.sadness = sadness;
                }

                public double getNeutral() {
                    return neutral;
                }

                public void setNeutral(double neutral) {
                    this.neutral = neutral;
                }

                public double getDisgust() {
                    return disgust;
                }

                public void setDisgust(double disgust) {
                    this.disgust = disgust;
                }

                public double getAnger() {
                    return anger;
                }

                public void setAnger(double anger) {
                    this.anger = anger;
                }

                public double getSurprise() {
                    return surprise;
                }

                public void setSurprise(double surprise) {
                    this.surprise = surprise;
                }

                public double getFear() {
                    return fear;
                }

                public void setFear(double fear) {
                    this.fear = fear;
                }

                public double getHappiness() {
                    return happiness;
                }

                public void setHappiness(double happiness) {
                    this.happiness = happiness;
                }
            }

            public static class GenderBean {
                /**
                 * value : Female
                 */

                private String value;

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }

            public static class AgeBean {
                /**
                 * value : 24
                 */

                private int value;

                public int getValue() {
                    return value;
                }

                public void setValue(int value) {
                    this.value = value;
                }
            }

            public static class BeautyBean {
                /**
                 * female_score : 83.954
                 * male_score : 79.424
                 */

                private double female_score;
                private double male_score;

                public double getFemale_score() {
                    return female_score;
                }

                public void setFemale_score(double female_score) {
                    this.female_score = female_score;
                }

                public double getMale_score() {
                    return male_score;
                }

                public void setMale_score(double male_score) {
                    this.male_score = male_score;
                }
            }

            public static class EthnicityBean {
                /**
                 * value : ASIAN
                 */

                private String value;

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }

        public static class FaceRectangleBean {
            /**
             * width : 1024
             * top : 438
             * left : 311
             * height : 1024
             */

            private int width;
            private int top;
            private int left;
            private int height;

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getTop() {
                return top;
            }

            public void setTop(int top) {
                this.top = top;
            }

            public int getLeft() {
                return left;
            }

            public void setLeft(int left) {
                this.left = left;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }
        }
    }
}
