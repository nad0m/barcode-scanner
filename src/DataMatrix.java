
/**
 * The DataMatrix class contains a BarcodeImage and a text string for the encoded message. 
 *
 */
class DataMatrix implements BarcodeIO
{
   public static final char BLACK_CHAR = '*';
   public static final char WHITE_CHAR = ' ';

   private BarcodeImage image;
   private String text;
   private int actualHeight;
   private int actualWidth;

   /**
    * Default constructor that initializes all variables and creates an empty
    * image.
    * 
    */
   DataMatrix()
   {
      this.image = new BarcodeImage();
      this.text = "";
      this.actualWidth = 0;
      this.actualHeight = 0;
   }

   /**
    * A constructor that scans an image.
    * 
    */
   DataMatrix(BarcodeImage image)
   {
      scan(image);
   }

   /**
    * A constructor that reads given text input.
    * 
    * @param text
    */
   DataMatrix(String text)
   {
      this();
      this.readText(text);
   }

   /**
    * Public method that creates a clone of the image and then "cleans" it.
    * Actual height and width of the barcode are also initialized.
    * 
    */
   public boolean scan(BarcodeImage barcode)
   {
      try
      {
         image = (BarcodeImage) barcode.clone();
         cleanImage();
         actualHeight = computeSignalHeight();
         actualWidth = computeSignalWidth();

      } catch (Exception e)
      {
         return false;
      }

      return true;
   }

   /**
    * Private method that guarantees any input image to be packed into the
    * bottom-left corner.
    * 
    */
   private void cleanImage()
   {
      boolean found = false;
      int heightOffset = 0;
      int widthOffset = 0;

      for (int i = BarcodeImage.MAX_HEIGHT - 1; i >= 0; i--)
      {
         for (int j = 0; j < BarcodeImage.MAX_WIDTH; j++)
         {
            if (image.getPixel(i, j) && found == false)
            {
               found = true;
               heightOffset = BarcodeImage.MAX_HEIGHT - 1 - i;
               widthOffset = j;
            }

            if (image.getPixel(i, j) && found)
            {
               movePixelToLowerLeft(i, j, heightOffset, widthOffset);
            }
         }
      }

   }

   /**
    * Translates the current pxel to the lower left.
    * 
    * @param row
    * @param col
    * @param heightOffset
    * @param widthOffset
    */
   private void movePixelToLowerLeft(int row, int col, int heightOffset,
      int widthOffset)
   {
      // turns current pixel "off"
      image.setPixel(row, col, false);

      // move pixel to bottom-left based on offset values
      image.setPixel(row + heightOffset, col - widthOffset, true);
   }

   /**
    * Computes the signal height
    * 
    * @return
    */
   private int computeSignalHeight()
   {
      int height = 0;

      for (int i = BarcodeImage.MAX_HEIGHT - 1; i >= 0; i--)
      {
         if (image.getPixel(i, 0))
         {
            height++;
         }
      }
      return height;
   }

   /**
    * Computes the signal Width
    * 
    * @return
    */
   private int computeSignalWidth()
   {
      int j = 0;
      for (j = 0; j < BarcodeImage.MAX_WIDTH; j++)
      {
         if (image.getPixel(BarcodeImage.MAX_HEIGHT - 1, j) == false)
         {
            return j;
         }
      }
      return j;
   }

   /**
    * returns the actualHeight
    * 
    * @return the height of the data.
    */
   public int getActualHeight()
   {
      return actualHeight;
   }

   /**
    * returns the width of the data.
    * 
    * @return actual data width
    */
   public int getActualWidth()
   {
      return actualWidth;
   }

   /**
    * Displays the image to console.
    */
   public void displayImageToConsole()
   {
      horizontalLines();

      for (int i = BarcodeImage.MAX_HEIGHT
         - actualHeight; i < BarcodeImage.MAX_HEIGHT; i++)
      {
         printValues(i);

         System.out.print("\n");
      }

   }

   /**
    * Prints the values in a row.
    * 
    * @param row
    *           to be printed
    */
   private void printValues(int row)
   {
      for (int j = 0; j <= actualWidth; j++)
      {
         if (j == 0)
         {
            System.out.print("|");
         } else if (j == actualWidth)
         {
            System.out.print("|");
         }

         if (image.getPixel(row, j))
         {
            System.out.print(BLACK_CHAR);
         } else
         {
            System.out.print(WHITE_CHAR);
         }
      }
   }

   /**
    * Public method that computes the barcode and save it into text string.
    * 
    */
   public boolean translateImageToText()
   {
      text = "";

      if (image == null)
      {
         return false;
      }

      for (int i = 1; i < actualWidth - 1; i++)
      {
         text += (char) getASCII(i);
      }

      return true;
   }

   /**
    * returns the asci value of this column
    * @param col
    * @return
    */
   private int getASCII(int col)
   {
      int sum = 0;
      int exponent = 0;

      for (int j = BarcodeImage.MAX_HEIGHT - 2; j > BarcodeImage.MAX_HEIGHT
         - actualHeight; j--, exponent++)
      {
         if (image.getPixel(j, col))
         {
            sum += (int) Math.pow(2, exponent);
         }
      }
      return sum;
   }

   public void displayTextToConsole()
   {
      System.out.println(text);
   }

   /**
    * creates horizontal borders.
    */
   private void horizontalLines()
   {
      for (int i = 0; i < actualWidth + 2; i++)
      {
         System.out.print("-");
      }
      System.out.print("\n");
   }

   /**
    * sets the text to be translated.
    */
   public boolean readText(String text)
   {
      this.text = text;
      this.image = new BarcodeImage();
      actualWidth = text.length() + 2;
      actualHeight = 10;
      return this.text == text;
   }

   /**
    * creates an image from the text
    */
   public boolean generateImageFromText()
   {
      //System.out.println(text);
      creatLimitationLines();
      for (int col = 1; col < text.length() + 1; col++)
      {
         writeCharToCol(col);
      }
      scan(image);
      return true;
   }

   /**
    * method to set the pixels of the image.
    * 
    * @param col
    */
   private void writeCharToCol(int col)
   {
      int charToConvert = (int) text.charAt(col - 1);
      for (int row = 8; row > 0; row--)
      {
         // Check if bit is set.
         if ((charToConvert & 1) == 1)
         {
            image.setPixel(row, col, true);
         } else
         {
            image.setPixel(row, col, false);
         }
         // shift bits
         charToConvert >>= 1;
      }
     // image.displayToConsole();
   }

   /**
    * creates the Closed Limitation Lines and Open border Lines
    */
   private void creatLimitationLines()
   {
      for (int row = 9; row >= 0; row--)
      {
         if (row == 9 || row == 0)
         {
            createHorizontalLimitLine(row);
         }
         createVerticalLimitationMark(row);
      }
   }

   /**
    * creates the horizontal Open Borderline and Closed Limitation Line
    * 
    * @param row
    */
   private void createHorizontalLimitLine(int row)
   {
      for (int col = 0; col < text.length() + 1; col++)
      {
         if (row == 9 || (row == 0 && col % 2 == 0))
         {
            image.setPixel(row, col, true);
         }
      }

   }

   /**
    * marks a point on the Vertical Limitation line and Open Border Line
    * 
    * @param row
    */
   private void createVerticalLimitationMark(int row)
   {
      image.setPixel(row, 0, true);
      if (row % 2 == 1)
      {
         image.setPixel(row, text.length() + 1, true);

      }
   }
}