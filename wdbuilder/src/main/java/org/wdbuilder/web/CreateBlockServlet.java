package org.wdbuilder.web;

import java.io.PrintWriter;

import javax.servlet.annotation.WebServlet;

import org.apache.commons.lang.StringUtils;
import org.wdbuilder.gui.IUIFormFactory;
import org.wdbuilder.gui.TwoColumnForm;
import org.wdbuilder.input.BlockParameter;
import org.wdbuilder.jaxbhtml.HtmlWriter;
import org.wdbuilder.plugin.IPluginFacade;
import org.wdbuilder.serialize.html.SectionHeader;
import org.wdbuilder.web.base.DiagramHelperFormServlet;
import org.wdbuilder.web.base.ServletInput;

@WebServlet("/create-block")
public class CreateBlockServlet extends DiagramHelperFormServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void do4DiagramHelperForm(ServletInput input) throws Exception {
		final PrintWriter writer = input.getResponse().getWriter();

		final String str = BlockParameter.DiagramKey.getString(input);
		final String diagramId = "'" + str + "'";

		final String blockClassStr = BlockParameter.BlockClass.getString(input);
		if (StringUtils.isEmpty(blockClassStr)) {
			return;
		}

		IPluginFacade pluginFacade = pluginFacadeRepository.getFacade(Class
				.forName(blockClassStr));
		if (null == pluginFacade) {
			return;
		}
		IUIFormFactory formFactory = pluginFacade.getFormFactory();

		String submitFunctionCall = formFactory
				.getCreateSubmitCall(BlockParameter.DiagramKey.getString(input));

		String closeHandler = "loadCanvas(" + diagramId + ",null)";

		final TwoColumnForm form = formFactory.getCreateHTML(
				diagramHelper.getDiagram().getKey()).addFooter(
				submitFunctionCall, closeHandler);

		new HtmlWriter(writer).write(new SectionHeader(formFactory
				.getCreateBlockTitle()));

		new HtmlWriter(writer).write(form);
	}

}
