package sb.lib.pikoshapeIt.lib

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import sb.lib.pikoshadowlayout.R

class PikoImageView @JvmOverloads constructor(context: Context,attr:AttributeSet?=null ,defStyle:Int =0) :View(context,attr,defStyle) {


    private var initialized =false


    private val mShaderMatrix = Matrix()


    init {

        init(context,attr,defStyle)



    }




    companion object {
        private const val  RECTANGLE = 0
        private const val CIRCLE = 1


    }

    private var top_left_cornor: Float = 0f
    private var top_right_cornor: Float = 0f
    private var bottom_left_cornor: Float = 0f
    private var bottom_right_cornor: Float = 0f




    private var pikoMarginLeft: Float =0f
    private var pikoMarginRight: Float =0f
    private var pikoMarginTop: Float =0f
    private var pikoMarginBottom: Float =0f

    private var radiiFloat: FloatArray= floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f)

    private lateinit var  derivedImage: Bitmap
    private val paintShadow: Paint = Paint().apply {


    }
    private val bitmapPaint: Paint= Paint().apply {

        this.color = Color.TRANSPARENT
          this.setShadowLayer(4f,3f,3f, Color.parseColor("#3300BCD4"))

    }


    private lateinit var bitmapImage: Bitmap
    private var imageDrawable :Drawable?=null

    private var shape :Int = 0

    private fun init(context: Context, attrs: AttributeSet?, defStyle: Int) {

        val customAttributes =
            context.obtainStyledAttributes(
                attrs,
                R.styleable.PikoImageView)


        try{

          imageDrawable =  customAttributes.getDrawable(R.styleable.PikoImageView_imageScr )




            top_left_cornor =   customAttributes.getDimension(R.styleable.PikoImageView_top_left_cornor_radius,0f)
            top_right_cornor =   customAttributes.getDimension(R.styleable.PikoImageView_top_right_cornor_radius ,0f)
            bottom_left_cornor =     customAttributes.getDimension(R.styleable.PikoImageView_bottom_left_cornor_radius ,0f)
            bottom_right_cornor =     customAttributes.getDimension(R.styleable.PikoImageView_bottom_right_cornor_radius ,0f)

            shape = customAttributes.getInt(R.styleable.PikoImageView_shape ,shape)



            if(shape== RECTANGLE) {
                pikoMarginLeft =
                    customAttributes.getDimension(R.styleable.PikoImageView_piko_margin_left, 0f)
                pikoMarginTop =
                    customAttributes.getDimension(R.styleable.PikoImageView_piko_margin_top, 0f)
                pikoMarginRight =
                    customAttributes.getDimension(R.styleable.PikoImageView_piko_margin_right, 0f)
                pikoMarginBottom =
                    customAttributes.getDimension(R.styleable.PikoImageView_piko_margin_bottom, 0f)
            }





            println("top left cornor ${top_left_cornor}")

            customAttributes.recycle()



        }catch (e:Exception){

            e.stackTrace

        }

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    derivedImage= Bitmap.createScaledBitmap(bitmapImage ,imageWidthSize ,imageHeightSize ,false )


        val shader =BitmapShader(derivedImage ,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP)
        paintShadow.shader = shader



        radiiFloat = floatArrayOf(top_left_cornor,top_left_cornor,top_right_cornor,top_right_cornor ,
            bottom_left_cornor,bottom_left_cornor,bottom_right_cornor,bottom_right_cornor)



        if(shape == CIRCLE){

            updateShaderMatrix()
            shader.setLocalMatrix(mShaderMatrix)

        }

        initialized =true


    }

    private var imageWidthSize = 0
    private var imageHeightSize =0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {


        val measureWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val measureHeightSize = MeasureSpec.getSize(heightMeasureSpec)



         bitmapImage =    drawableToBitmap(imageDrawable!!)!!

        val widthMargin = pikoMarginLeft + pikoMarginRight
        val heightMargin = pikoMarginTop + pikoMarginBottom



        imageWidthSize = Math.min(bitmapImage.width - widthMargin.toInt()  ,measureWidthSize- widthMargin.toInt())
        imageHeightSize = Math.min(bitmapImage.height - heightMargin.toInt() ,measureHeightSize - heightMargin.toInt())

        println("width Margin  ${imageWidthSize}")

        setMeasuredDimension(imageWidthSize,imageHeightSize)

    }


    private var path = Path()


    override fun onDraw(canvas: Canvas?) {

        if(canvas ==null)return



        if(!initialized) return


        if(shape == RECTANGLE) {

            drawRectShape(canvas)
        }else{


            drawCircleShape(canvas)

        }


    }

    private fun drawCircleShape(canvas: Canvas) {


        val imageRatio = Math.min(imageWidthSize,imageHeightSize)


        canvas.drawCircle(imageWidthSize/2f,imageHeightSize/2f,imageRatio/2f , paintShadow)
    }

    private fun updateShaderMatrix() {
        val scale: Float
        var dx = 0f
        var dy = 0f
        mShaderMatrix.set(null)
        val bitmapHeight: Int = bitmapImage.height
        val bitmapWidth: Int = bitmapImage.width
        if (bitmapWidth * imageHeightSize > imageWidthSize * bitmapHeight) {
            scale = imageHeightSize / bitmapHeight.toFloat()
            dx = (imageWidthSize - bitmapWidth * scale) * 0.5f
        } else {
            scale = imageWidthSize / bitmapWidth.toFloat()
            dy = ((imageHeightSize) - bitmapHeight * scale) * 0.5f
        }
        mShaderMatrix.setScale(0.5f, 0.5f)
        mShaderMatrix.postTranslate(120f,350f)


    }

    private fun drawRectShape(canvas: Canvas) {


        path.addRoundRect(
            pikoMarginLeft,
            pikoMarginTop,
            imageWidthSize.toFloat() + pikoMarginLeft,
            imageHeightSize.toFloat() - pikoMarginTop,
            radiiFloat,
            Path.Direction.CW
        )

        canvas.drawPath(path, paintShadow)



    }


    /*************** Drawable into ImageView *******/

    private  fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            ) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }




}