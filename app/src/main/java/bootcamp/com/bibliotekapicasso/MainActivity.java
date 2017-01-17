package bootcamp.com.bibliotekapicasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.imageView6)
    ImageView imageView;

    @BindView(R.id.imageView7)
    ImageView imageView7;

    @BindView(R.id.imageView8)
    ImageView imageView8;

    @BindView(R.id.imageView9)
    ImageView imageView9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //Wstawiamy obraz, metoda with() przyjmuje kontekst za argument
        Picasso.with(this)
                //Podajemy URL, package lub ścieżkę do pliku z obrazkiem
                .load("http://media.istockphoto.com/vectors/sci-fi-futuristic-user-interface-vector-id516546506?s=2048x2048")
                //Zmiana rozmiaru obrazka. Wartości w pikselach.
                .resize(450/*szerokość*/, 450/*wysokość*/)
                //Przycina obrazek tak, by mieścił się w swoim widgecie i wypełniał go w całości (wymaga użycia resize())
                .centerCrop()
                //Obrazek, który będzie pokazywany podczas ładowania tego właściwego
                .placeholder(R.drawable.ic_file_download_grey_500_48dp)
                //Obrazek do wyświetlenia w razie błędu ładowania
                .error(R.drawable.ic_broken_image_grey_400_48dp)
                //Layout, do którego trafi obrazek
                .into(imageView);
        Picasso.with(this)
                .load("http://media.istockphoto.com/photos/debugging-a-few-lines-of-code-picture-id529418972?s=2048x2048")
                .resize(630, 450)
                .centerCrop()
                .placeholder(R.drawable.ic_file_download_grey_500_48dp)
                .error(R.drawable.ic_broken_image_grey_400_48dp)
                .into(imageView7);
        Picasso.with(this)
                .load("http://media.istockphoto.com/vectors/abstract-hud-futuristic-background-vector-illustration-vector-id482299460?s=2048x2048")
                //.rotate(45)
                .resize(435, 435)
                .placeholder(R.drawable.ic_file_download_grey_500_48dp)
                .error(R.drawable.ic_broken_image_grey_400_48dp)
                //Wykonuje zadaną transformację obrazka, z użyciem wskazanego filtru, wymaga implementacji
                .transform(new BlurTransform(this))
                .into(imageView8);
        Picasso.with(this)
                .load(R.drawable.img)
                .resize(1080, 600)
                .placeholder(R.drawable.ic_file_download_grey_500_48dp)
                .error(R.drawable.ic_broken_image_grey_400_48dp)
                .into(imageView9);

    }

    private class BlurTransform implements com.squareup.picasso.Transformation{

        RenderScript renderScript;

        public BlurTransform(Context context){
            super();
            renderScript = RenderScript.create(context);
        }

        @Override
        public Bitmap transform(Bitmap source) {

            //Tworzy nową bitmapę, która zachowa rezultat transformacji
            Bitmap blurredBitmap = source.copy(Bitmap.Config.ARGB_8888, true);

            //Alokuje pamięć dla renderera
            Allocation input = Allocation.createFromBitmap(renderScript, blurredBitmap, Allocation.MipmapControl.MIPMAP_FULL, Allocation.USAGE_SHARED);
            Allocation output = Allocation.createTyped(renderScript, input.getType());

            //Tworzy instancję skryptu który chcemy użyć
            ScriptIntrinsicBlur bluringScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            bluringScript.setInput(input);

            //Ustawia promień efektu rozmazania
            bluringScript.setRadius(15);

            //Uruchamia skrypt, który stworzy efekt rozmazania
            bluringScript.forEach(output);

            //Kopiuje efekt pracy skryptu do bitmapy, która ma przechwywać efekt transformacji
            output.copyTo(blurredBitmap);

            //Zachowuje efekt transformacji
            source.recycle();

            //Zwraca gotową bitmapę po transformacji
            return blurredBitmap;
        }

        @Override
        public String key() {
            return "blur";
        }
    }
}
