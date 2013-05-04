package org.wdbuilder.view;

import static org.wdbuilder.service.DiagramService.RESIZE_AREA;
import static org.wdbuilder.util.ImageUtility.getImageObserver;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;

import javax.imageio.ImageIO;

import org.wdbuilder.domain.Block;
import org.wdbuilder.domain.Diagram;
import org.wdbuilder.domain.DiagramEntity;
import org.wdbuilder.domain.Link;
import org.wdbuilder.plugin.IPluginFacade;
import org.wdbuilder.plugin.IRenderContext;
import org.wdbuilder.plugin.IRenderer;
import org.wdbuilder.utility.DiagramHelper;
import org.wdbuilder.utility.IPluginFacadeRepository;
import org.wdbuilder.web.ApplicationState;

import com.google.common.io.Resources;

public class DiagramRenderer implements IRenderer {

	private final ApplicationState appState;
	private final IPluginFacadeRepository pluginFacadeRepository;

	public DiagramRenderer(ApplicationState appState,
			IPluginFacadeRepository pluginFacadeRepository) {
		this.appState = appState;
		this.pluginFacadeRepository = pluginFacadeRepository;
	}

	@Override
	public void draw(DiagramEntity entity, IRenderContext diagramRenderCtx) {
		if (!Diagram.class.isInstance(entity)) {
			return;
		}
		Diagram diagram = Diagram.class.cast(entity);

		Graphics2D gr = diagramRenderCtx.getGraphics();

		final Rectangle rect = new Rectangle(diagramRenderCtx.getOffset()
				.toAWT(), diagram.getSize().toAWT());
		new SimpleBackgroundRenderer(diagram.getBackground()).render(gr, rect);

		if (appState.isBlockMode()) {
			// Load "resize corner":
			Image resizeCornerImage = loadResizeCornerImage(appState);
			if (null != resizeCornerImage) {
				int x = diagram.getSize().getWidth() - RESIZE_AREA.getWidth();
				int y = diagram.getSize().getHeight() - RESIZE_AREA.getHeight();
				gr.drawImage(resizeCornerImage, x, y, getImageObserver());
			}
		}

		for (final Block block : diagram.getBlocks()) {
			final RenderContext blockCtx = new RenderContext(diagramRenderCtx,
					appState);
			blockCtx.setOpaque(true);
			blockCtx.getOffset().setX(block.getLocation().getX());
			blockCtx.getOffset().setY(block.getLocation().getY());
			blockCtx.setGraphics(gr);

			IPluginFacade pluginFacade = pluginFacadeRepository
					.getFacade(block.getClass());
			IRenderer renderer = pluginFacade.getRenderer();
			renderer.draw(block, blockCtx);
		}

		gr.setColor(Color.black);
		final DiagramHelper diagramHelper = new DiagramHelper(diagram);
		for (final Link link : diagram.getLinks()) {
			new LinkRenderer(gr, link, diagramHelper.findBlockByKey(link
					.getBeginKey()), diagramHelper.findBlockByKey(link
					.getEndKey())).render(appState.getMode());
		}
	}

	private static Image loadResizeCornerImage(ApplicationState appState) {
		try {
			URL imageURL = Resources.getResource("images/resize-corner.png");
			Image result = ImageIO.read(imageURL);
			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
