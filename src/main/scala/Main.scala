import ai.djl.ModelException
import ai.djl.modality.cv.transform.{Resize, ToTensor}
import ai.djl.modality.cv.{Image, ImageFactory}
import ai.djl.training.util.ProgressBar
import ai.djl.translate.{Pipeline, TranslateException}
import ai.djl.util.RandomUtils
import org.opencv.core._
import org.opencv.highgui.HighGui
import org.opencv.videoio.VideoCapture

import java.awt.Color
import java.awt.image.DataBufferByte
import java.nio.file.{Files, Paths}
import java.util




object Main {

  import org.opencv.core.Scalar

  val rect = new Rect()
  val color = new Scalar(0, 255, 0)

  import ai.djl.modality.cv.output.DetectedObjects
  import ai.djl.modality.cv.translator.YoloV5Translator
  import ai.djl.repository.zoo.ModelZoo

  //
  import org.opencv.core.Mat

  import java.awt.image.BufferedImage

  def randomColor(): Color = {
    new Color(RandomUtils.nextInt(255))
  }

  def drawBoundingBoxes(frame : Mat , detections: DetectedObjects): Unit = {

    val list: util.List[DetectedObjects.DetectedObject] = detections.items
    list.forEach { obj =>
      val bbox = obj.getBoundingBox
      val rectangle = bbox.getBounds
      val showText = String.format("%s: %.2f", obj.getClassName, obj.getProbability)
      rect.x = rectangle.getX.toInt
      rect.y = rectangle.getY.toInt
      rect.width = rectangle.getWidth.toInt
      rect.height = rectangle.getHeight.toInt
      import org.opencv.imgproc.Imgproc
      Imgproc.rectangle(frame, rect, color, 2)
      Imgproc.putText(frame, showText,
        new Point(rect.x, rect.y),
        Imgproc.FONT_HERSHEY_COMPLEX,
        rectangle.getWidth / 200, color)
    }
  }
    /* val g: Graphics2D = image.getGraphics.asInstanceOf[Graphics2D]
    val stroke: Int = 2
    g.setStroke(new BasicStroke(stroke))
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
    val imageWidth: Int = image.getWidth
    val imageHeight: Int = image.getHeight
    val list: util.List[DetectedObjects.DetectedObject] = detections.items
    list.forEach { result =>
      val className: String = result.getClassName
      val box: BoundingBox = result.getBoundingBox
      g.setPaint(randomColor().darker)
      val rectangle = box.getBounds
      val x: Int = rectangle.getX.toInt
      val y: Int = rectangle.getY.toInt
      g.drawRect(x, y, rectangle.getWidth.toInt, rectangle.getHeight.toInt)
      // drawText(g, className, x, y, stroke, 4)

    }
    */

  def Mat2BufferedImage(m: Mat): BufferedImage = { // Fastest code
    // output can be assigned either to a BufferedImage or to an Image
    var `type` = BufferedImage.TYPE_BYTE_GRAY
    if (m.channels > 1) `type` = BufferedImage.TYPE_3BYTE_BGR
    val bufferSize = m.channels * m.cols * m.rows
    val b = new Array[Byte](bufferSize)
    m.get(0, 0, b) // get all the pixels

    val image = new BufferedImage(m.cols, m.rows, `type`)
    val targetPixels = image.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData
    System.arraycopy(b, 0, targetPixels, 0, b.length)
    image
  }

  def detect(im: Mat): Unit = {
    //   resize(frame, frame, new Size(416,  416))
    val imageSize = 640
    val pipeline = new Pipeline()
    pipeline.add(new Resize(imageSize))
    pipeline.add(new ToTensor())
     val frame = Mat2BufferedImage(im)
    //var img2 = ImageFactory.getInstance().fromImage(img)
    //  val translator = YoloV5Translator.builder.build.asInstanceOf[Translator[Image, DetectedObjects]]
    import ai.djl.repository.zoo.Criteria
    val translator = YoloV5Translator
      .builder()
      .setPipeline(pipeline)
      .optSynsetArtifactName("coco.names") //数据的labels文件名称
      //.optSynset(Arrays.asList("qinggangwa","dapeng","dapengs"))
      //预测的最小下限
      .optThreshold(0.4f)
      .build();


    val criteria = {
      Criteria.builder
        .setTypes(classOf[Image], classOf[DetectedObjects])
        .optModelUrls(Paths.get("model/").toFile().getPath()) //模型的路径
        .optModelName("yolov5s.torchscript.pt") //模型的文件名称
        .optTranslator(translator) //设置Translator
        .optProgress(new ProgressBar()) //展示加载进度
        .build();
    }
    //   println("je passe ici")
    val model = ModelZoo.loadModel(criteria)
    // println(model.
    // var predictor: Option[Predictor[Image, DetectedObjects]] = None
    val img: Image = ImageFactory.getInstance.fromImage(frame)
    val predictor = model.newPredictor()
    val results = predictor.predict(img)
    println(results)
    val outputDir = Paths.get("output/")
    Files.createDirectories(outputDir)
    drawBoundingBoxes(im, results)
    val imagePath = outputDir.resolve("detected-5.jpg")
    // OpenJDK can't save jpg with alpha channel
    img.save(Files.newOutputStream(imagePath), "jpg")
    println("Detected objects image has been saved in: {}", imagePath)
    // println(predictor.toString)
  }

  def main(args: Array[String]): Unit = {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME)

    try {
      val cap = new VideoCapture(0)
      if (!cap.isOpened) { // IsOpened function is used to determine if the camera is successful
        System.out.println("Camera Error") // If the camera is invoked, output an error message
      } else {
        val frame = new Mat()
        // Create an output frame
        var flag = cap.read(frame) // read method reads the current frame of the camera
        while (flag) {
          detect(frame);
          HighGui.imshow("yolov5", frame)
          HighGui.waitKey(30)
          flag = cap.read(frame)
        }
      }
    } catch {
      case t: RuntimeException => println(t.toString)
      case t: ModelException => println(t.toString)
      case t: TranslateException => println(t.toString)
      case t: Throwable => println(t.toString)
    }
  }
}