package de.markusfisch.android.shadereditor.fragment;

import de.markusfisch.android.shadereditor.app.ShaderEditorApplication;
import de.markusfisch.android.shadereditor.graphics.BitmapEditor;
import de.markusfisch.android.shadereditor.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Sampler2dPropertiesFragment extends SamplerPropertiesFragment
{
	private static final String IMAGE_URI = "image_uri";
	private static final String CROP_RECT = "crop_rect";
	private static final String ROTATION = "rotation";

	private Uri imageUri;
	private RectF cropRect;
	private float imageRotation;

	public static Fragment newInstance(
		Uri uri,
		RectF rect,
		float rotation )
	{
		Bundle args = new Bundle();
		args.putParcelable( IMAGE_URI, uri );
		args.putParcelable( CROP_RECT, rect );
		args.putFloat( ROTATION, rotation );

		Sampler2dPropertiesFragment fragment =
			new Sampler2dPropertiesFragment();
		fragment.setArguments( args );

		return fragment;
	}

	@Override
	public View onCreateView(
		LayoutInflater inflater,
		ViewGroup container,
		Bundle state )
	{
		Activity activity;

		if( (activity = getActivity()) == null )
			return null;

		activity.setTitle( R.string.texture_properties );

		Bundle args;
		View view;

		if( (args = getArguments()) == null ||
			(imageUri = args.getParcelable(
				IMAGE_URI )) == null ||
			(cropRect = args.getParcelable(
				CROP_RECT )) == null ||
			(view = initView(
				activity,
				inflater,
				container )) == null )
		{
			activity.finish();
			return null;
		}

		imageRotation = args.getFloat( ROTATION );

		setSamplerType( "sampler2D" );

		return view;
	}

	@Override
	protected int saveSampler(
		Context context,
		String name,
		int size )
	{
		return saveTexture(
			// try to get a bigger source image in
			// case the cut out is quite small
			BitmapEditor.getBitmapFromUri(
				context,
				imageUri,
				// which doesn't work for some devices;
				// 2048 is too much => out of memory
				1024 ),
			cropRect,
			imageRotation,
			name,
			size );
	}

	private int saveTexture(
		Bitmap bitmap,
		RectF rect,
		float rotation,
		String name,
		int size )
	{
		if( (bitmap = BitmapEditor.crop(
			bitmap,
			rect,
			rotation )) == null )
			return R.string.illegal_rectangle;

		if( ShaderEditorApplication
			.dataSource
			.insertTexture(
				name,
				Bitmap.createScaledBitmap(
					bitmap,
					size,
					size,
					true ) ) < 1 )
			return R.string.name_already_taken;

		return 0;
	}
}