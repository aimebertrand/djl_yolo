import ai.djl.engine.Engine
object Main {

//  DownloadUtils.download("https://djl-ai.s3.amazonaws.com/mlrepo/model/cv/image_classification/ai/djl/pytorch/resnet/0.0.1/traced_resnet18.pt.gz", "build/pytorch_models/resnet18/resnet18.pt", new ProgressBar)
 // DownloadUtils.download("https://djl-ai.s3.amazonaws.com/mlrepo/model/cv/image_classification/ai/djl/pytorch/synset.txt", "build/pytorch_models/resnet18/synset.txt", new ProgressBar)
  def main(args: Array[String]): Unit = {


    Engine.debugEnvironment()
   /*
    val translator = ImageClassificationTranslator.builder
      .addTransform(new Resize(256)).addTransform(new CenterCrop(224, 224))
      .addTransform(new ToTensor)
      .addTransform(new Normalize(Array[Float](0.485f, 0.456f, 0.406f), Array[Float](0.229f, 0.224f, 0.225f)))
      .optApplySoftmax(true).build

    val criteria = Criteria.builder.setTypes(classOf[Image], classOf[Classifications])
      .optModelPath(Paths.get("build/pytorch_models/resnet18"))
      .optTranslator(translator).optProgress(new ProgressBar).build

    val model = criteria.loadModel
    import ai.djl.modality.cv.ImageFactory
    val img = ImageFactory.getInstance.fromUrl("https://raw.githubusercontent.com/pytorch/hub/master/images/dog.jpg")
    val predictor = model.newPredictor
    val classifications = predictor.predict(img)
    println(classifications)
    */
 }
}