#Kama for Android

Kama for Android is a library for easy executing HTTP requests, and parsing their JSON response into Pojo's. It uses [Jackson](https://github.com/FasterXML/jackson) to map JSON to Plain Old Java Objects.

##Setup

To use this library, make the following changes:

 * To `settings.gradle`:

        include ':kama'

        project(':kama').projectDir = new File(settingsDir, 'path/to/kama/app')

 * Your app's `build.gradle`:

        dependencies {
            compile project(':kama')
        }

        android {
            packagingOptions {
                exclude 'META-INF/LICENSE.txt'
                exclude 'META-INF/LICENSE'
                exclude 'META-INF/NOTICE.txt'
                exclude 'META-INF/NOTICE'
                exclude 'LICENSE.txt'
            }
        }

##Usage

###POJO's
A **P**lain **O**ld **J**ava **O**bject needs to be formatted according to the Jackson library as follows:

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class MyObject {
    
        @JsonProperty("name")
        private String name;

    } 

###Json
You can execute HTTP requests and parse their responses by using the `JsonGetter`, `JsonPoster`, `JsonDeleter` and `JsonPutter` classes. Do not forget to provide the `Class` parameter in the constructor! 

Example:

    /* Retrieve and parse a single object */
    JsonGetter<MyObject> jsonGetter = new JsonGetter<MyObject>(MyObject.class);
    jsonGetter.setUrl(MY_URL);
    jsonGetter.setJsonTitle("my_object");
    jsonGetter.setUrlData(myUrlData);

    MyObject myObject = jsonGetter.execute();

    /* Retrieve and parse a single object */
    JsonGetter<MyObject> jsonGetter = new JsonGetter<MyObject>(MyObject.class);
    jsonGetter.setUrl(MY_URL);
    jsonGetter.setJsonTitle("my_object");
    jsonGetter.setUrlData(myUrlData);

    List<MyObject> myObject = jsonGetter.executeReturnsObjectsList();

The `Poster`, `Deleter` and `Putter` classes work similarly.

If there is no parseable response, use `Void`:

    /* Retrieve and parse a single object */
    JsonDeleter<Void> jsonDeleter = new JsonDeleter<Void>();
    jsonDeleter.setUrl(MY_URL);
    jsonDeleter.setUrlData(myUrlData);

    jsonDeleter.execute(); //returns null

If you forget to pass the class in the constructor, the execute methods will return `null`!

If the response code is not `200` or `202`, a `HttpResponseKamaException` is thrown, including the meta data, stored in an instance of `KamaError` class.

###Kama formatted JSON
A Kama formatted JSON response looks like this:

    {
        "meta": {
            "code": 200
        },
        "response": {
            "my_objects": []
        }
    }

You can use the classes `KamaGetter`, `KamaPoster`, `KamaDeleter` and `KamaPutter` to do Kama requests. These classes add the necessary headers and url parameters for each request.
