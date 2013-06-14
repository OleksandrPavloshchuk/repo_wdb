package org.wdbuilder.serialize.html;

import java.awt.Rectangle;
import java.util.Collection;

import org.wdbuilder.domain.Block;
import org.wdbuilder.domain.Diagram;
import org.wdbuilder.domain.Link;
import org.wdbuilder.domain.LinkSocket;
import org.wdbuilder.domain.helper.Point;
import org.wdbuilder.jaxbhtml.element.Area;
import org.wdbuilder.service.DiagramService;

public class LineDiagramImageMap extends DiagramImageMap {

  private final String jsDragStartMethod;

	protected LineDiagramImageMap(Diagram diagram, String jsDragStartMethod) {
		super(diagram);
		this.jsDragStartMethod = jsDragStartMethod;

		final Collection<Block> blocks = diagramHelper.getDiagram().getBlocks();
		if (null != blocks) {
			for (final Block entity : blocks) {
				createLinkSocketAreasForBlock(entity);
			}
		}
		final Collection<Link> links = diagramHelper.getDiagram().getLinks();
		if (null != links) {
			for (final Link link : links) {
				add(createLinkPivotArea(link));
			}
		}
	}

	private Area.Rect createLinkPivotArea(Link link) {
		final Point pivot = link.getPivot();
		final Point topLeft = new Point(pivot.getX()
				- DiagramService.LINE_AREA.getWidth() / 2, pivot.getY()
				- DiagramService.LINE_AREA.getHeight() / 2);

		final Area.Rect area = new Area.Rect(topLeft.toAWT(),
				DiagramService.LINE_AREA.toAWT());
		area.setOnMouseDown(createOnMouseDownHandler(link));
		area.setTitle(link.getKey());
		return area;
	}

	private String createOnMouseDownHandler(Link link) {
		final LinkSocket beginSocket = link.getSockets().get(0);
		final LinkSocket endSocket = link.getSockets().get(1);

		boolean isHorizontal = beginSocket.isHorizontal();

		Point beginPoint = diagramHelper.getOffset(beginSocket);
		Point endPoint = diagramHelper.getOffset(endSocket);

		final String result = getOnMouseDownFunctionCall(
				"WDB.LinkArrange.mouseDown", diagramHelper.getDiagram()
						.getKey(), link.getKey(), beginPoint, endPoint,
				isHorizontal);
		return result;
	}

	private void createLinkSocketAreasForBlock(Block block) {
		final Collection<LinkSocket> sockets = LinkSocket.getAvailable(
				diagramHelper.getUsedLinkSockets(block), block);

		// Add possible line start and end points
		for (final LinkSocket socket : sockets) {
			Rectangle rect = socket.getArea(block);

			String id = block.getKey() + ":"
					+ String.valueOf(socket.getDirection()) + ":"
					+ socket.getIndex();

			String onMouseDown = getOnMouseDownFunctionCall(jsDragStartMethod,
					diagramHelper.getDiagram().getKey(), id,
					block.getLocation());

			Area.Rect area = new Area.Rect(rect.getLocation(), rect.getSize());
			area.setOnMouseDown(onMouseDown);
			area.setTitle(block.getName());
			area.setId(id);
			add(area);
		}
	}

}
