
package org.micromanager.bfcorrector;

import ij.ImagePlus;
import ij.process.ImageStatistics;
import mmcorej.TaggedImage;
import org.json.JSONException;
import org.json.JSONObject;
import org.micromanager.acquisition.TaggedImageQueue;
import org.micromanager.api.DataProcessor;
import org.micromanager.utils.MDUtils;
import org.micromanager.utils.MMScriptException;
import org.micromanager.utils.ReportingUtils;

/**
 *
 * @author nico
 */
class BFProcessor extends DataProcessor<TaggedImage> {
   private ImagePlus flatField_;
   private ImageStatistics flatFieldStats_;
   private int flatFieldWidth_;
   private int flatFieldHeight_;
   private int flatFieldType_;
   private float[] normalizedFlatField_;
   
   
   /**
    * Set the flatfield image that will be used in flatfielding
    * Set to null if no flatfielding is desired
    * 
    * @param flatField ImagePlus object representing the flatfield image 
    */
   public void setFlatField(ImagePlus flatField) {
      flatField_ = flatField;
      
      if (flatField != null) {
         flatFieldStats_ = ImageStatistics.getStatistics(flatField.getProcessor(),
                 ImageStatistics.MEAN + ImageStatistics.MIN_MAX, null);
         flatFieldWidth_ = flatField_.getWidth();
         flatFieldHeight_ = flatField_.getHeight();
         flatFieldType_ = flatField_.getType();
         normalizedFlatField_ = new float[flatFieldWidth_ * flatFieldHeight_];
         float mean = (float) flatFieldStats_.mean;
         for (int x = 0; x < flatFieldWidth_; x++) {
            for (int y = 0; y < flatFieldHeight_; y++) {
               int index = (y * flatFieldWidth_) + x;
               normalizedFlatField_[index] =  
                       flatField.getProcessor().getf(index) / mean;
            }
         }
         
      } else {
         flatField_ = null;
      }
      
   }
   
   /**
    * Polls for tagged images, and processes them if they are from the selected 
    * camera.
    * 
    */
   @Override
   public void process() {
      try {
         TaggedImage nextImage = poll();
         if (nextImage != TaggedImageQueue.POISON) {
            try {

               produce(proccessTaggedImage(nextImage));

            } catch (Exception ex) {
               produce(nextImage);
               ReportingUtils.logError(ex);
            }
         } else {
            //Must produce Poison image so LiveAcq Thread terminates properly
            produce(nextImage);
         }
      } catch (Exception ex) {
         ReportingUtils.logError(ex);
      }
   }

   /**
    * Executes flatfielding
    * 
    * First mirror the image if requested, than rotate as requested
    * 
    * @return - Transformed tagged image, otherwise a copy of the input
    * @throws JSONException
    * @throws MMScriptException 
    */
   public  TaggedImage proccessTaggedImage(TaggedImage nextImage) throws JSONException, MMScriptException {

      if (flatField_ == null) {
         return nextImage;
      }
      
      int width = MDUtils.getWidth(nextImage.tags);
      int height = MDUtils.getHeight(nextImage.tags);
      String type = MDUtils.getPixelType(nextImage.tags);
      int ijType = ImagePlus.GRAY8;
      if (type.equals("GRAY16")) {
         ijType = ImagePlus.GRAY16;
      }
      
      if (! (ijType == ImagePlus.GRAY8 || ijType == ImagePlus.GRAY16) ) {
         // Report???
         return nextImage;
      }
      
      // do not calculate if image size differs
      if (width != flatFieldWidth_ || height != flatFieldHeight_) {
         ReportingUtils.logError("FlatField dimensions do not match image dimensions");
         return nextImage;
      }
      
      JSONObject newTags = nextImage.tags;
      TaggedImage newImage = null;
      
      if (ijType == ImagePlus.GRAY8) {
         byte[] newPixels = new byte[width * height];
         byte[] oldPixels = (byte[]) nextImage.pix;
         for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
               int index = (y * flatFieldWidth_) + x;
               newPixels[index] = (byte) ( (float) oldPixels[index] 
                       / normalizedFlatField_[index]);
            }
         }
         newImage = new TaggedImage(newPixels, newTags);
      } else if (ijType == ImagePlus.GRAY16) {
         short[] newPixels = new short[width * height];
         short[] oldPixels = (short[]) nextImage.pix;
         for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
               int index = (y * flatFieldWidth_) + x;
               newPixels[index] = (short) ( (float) oldPixels[index] 
                       / normalizedFlatField_[index]);
            }
         }
         newImage = new TaggedImage(newPixels, newTags);
      }
      

      return newImage;
   }
   
}
