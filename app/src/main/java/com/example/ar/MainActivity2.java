package com.example.ar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
Button Gallery,AR;
    private static final int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView imageview;
    private ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Gallery=findViewById(R.id.select);
        AR=findViewById(R.id.ar);
        imageview=findViewById(R.id.imageView);

        AR.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent myIntent = new Intent(MainActivity2.this, MainActivity.class);
                MainActivity2.this.startActivity(myIntent);
            }
        });

        Gallery.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openGallery();
            }
        });


    }
    private void openGallery()
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {
            imageUri = data.getData();
            imageview.setImageURI(imageUri);

            setContentView(R.layout.activity_main);
            arFragment= (ArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
            assert arFragment != null;
            arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
                Anchor anchor =hitResult.createAnchor();
                createViewRenderable(hitResult.createAnchor());
            });

        }

    }
    private void addModeltoScene(Anchor anchor, ModelRenderable modelRenderable)
    {
        AnchorNode anchorNode=new AnchorNode( anchor);
        TransformableNode transformableNode=new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }
    private void createViewRenderable(Anchor anchor)
    {
        ViewRenderable
                .builder()
                .setView(this,R.layout.text)
                .build()
                .thenAccept(viewRenderable -> {
                    addtoScene(viewRenderable, anchor);
                });
    }
    private void addtoScene(ViewRenderable viewRenderable, Anchor anchor)
    {
        AnchorNode anchorNode=new AnchorNode(anchor);
        anchorNode.setRenderable(viewRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        View view=viewRenderable.getView();
        ViewPager viewPager=view.findViewById(R.id.viewPager);
        List<Uri> images =new ArrayList<>();
        images.add(imageUri);
        Adapter adapter=new Adapter(images);
        viewPager.setAdapter(adapter);


    }

    class Adapter extends PagerAdapter
    {
        List<Uri> images;
        Adapter(List<Uri>images)
        {
            this.images=images;
        }
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position)
        {
            View view= getLayoutInflater().inflate(R.layout.item,container,false);
            ImageView imageView=view.findViewById(R.id.imageView);
            imageView.setImageURI(imageUri);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
        {
            container.removeView((ImageView)object);
        }

        @Override
        public int getCount()
        {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }
}
