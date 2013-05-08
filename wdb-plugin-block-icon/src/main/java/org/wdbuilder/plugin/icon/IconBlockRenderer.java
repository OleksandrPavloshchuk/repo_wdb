package org.wdbuilder.plugin.icon;

import static org.wdbuilder.util.ImageUtility.getImageObserver;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.wdbuilder.domain.Block;
import org.wdbuilder.plugin.IRenderContext;
import org.wdbuilder.plugin.icon.domain.IconBlock;
import org.wdbuilder.view.BlockRenderer;

import com.google.common.io.Resources;

class IconBlockRenderer extends BlockRenderer<Block> {

	private static final Logger LOG = Logger.getLogger(IconBlockRenderer.class);

	@Override
	public void draw(Block blockRaw, IRenderContext renderCtx) {

		if (!IconBlock.class.isInstance(blockRaw)) {
			return;
		}
		IconBlock block = IconBlock.class.cast(blockRaw);

		// Use additional image:
		Image blockImage = null;
		URL imageURL = Resources.getResource(block.getIcon().getPath());
		try {
			blockImage = ImageIO.read(imageURL);
		} catch (Exception ex) {
			LOG.error("can't load image: " + imageURL, ex);
			return;
		}

		Graphics2D gr = renderCtx.getGraphics();

		if (!renderCtx.isOpaque()) {
			gr.setComposite(AlphaComposite
					.getInstance(AlphaComposite.SRC, 0.5f));
		}

		// TODO (2012/06/03) move it to the higher level
		java.awt.Dimension size = block.getSize().toAWT();
		int x = renderCtx.getOffset().getX() - size.width / 2;
		int y = renderCtx.getOffset().getY() - size.height / 2;

		// Copy the image:
		gr.drawImage(blockImage, x + MARGIN, y + MARGIN, getImageObserver());

		drawText(gr, block, x, y);

		if (renderCtx.isBlockMode() && renderCtx.isSelected(block)) {
			gr.setColor(COLOR_SELECTION);

			gr.drawRect(x, y, size.width - 1, size.height - 1);
			gr.drawRect(x + 1, y + 1, size.width - 3, size.height - 3);
		}

		renderLinkSockets(block, renderCtx);
	}

	private static void drawText(Graphics2D gr, Block block, int offsetX,
			int offsetY) {
		gr.setColor(Color.black);
		gr.setFont(FONT);
		final String str = block.getName();
		final FontMetrics metrics = gr.getFontMetrics(FONT);
		final int width = metrics.stringWidth(str);
		final int height = metrics.getHeight();

		final int centerX = offsetX + block.getSize().getWidth() / 2;
		final int centerY = offsetY + block.getSize().getHeight();

		gr.drawString(str, centerX - width / 2, centerY - height / 2);
	}
}
