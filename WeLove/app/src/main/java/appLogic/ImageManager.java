package appLogic;

/**
 * Created by Long on 5/10/2015.
 */
public class ImageManager {
    public static String getImageLocalPath(String imageUrl, String id)
    {
        String extension = imageUrl.substring(imageUrl.lastIndexOf('.'));
        return AppConstant.imageFolder + "/photo_" + id + extension;
    }
}