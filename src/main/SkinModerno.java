package main;

import javax.swing.Icon;

import ar.com.fwcommon.templates.main.skin.AbstractSkin;
import ar.com.fwcommon.util.ImageUtil;

public class SkinModerno extends AbstractSkin {

	public SkinModerno(ESkin eskin) {
		super(eskin.getPath(), new SkinDecoratorModerno(), "Estilo Moderno");
	}

	@Override
	public boolean isSkinLF() {
		return true;
	}

	@Override
	public Icon getPreview() {
		return ImageUtil.loadIcon(SkinModerno.class, "main/preview_moderno.png");
	}
}