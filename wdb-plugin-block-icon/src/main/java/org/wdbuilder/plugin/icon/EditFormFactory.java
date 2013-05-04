package org.wdbuilder.plugin.icon;

import java.util.ArrayList;
import java.util.List;

import org.wdbuilder.domain.Block;
import org.wdbuilder.gui.PredefinedSelect;
import org.wdbuilder.gui.TwoColumnForm;
import org.wdbuilder.gui.UIExistingBlockFormFactory;
import org.wdbuilder.input.BlockParameter;
import org.wdbuilder.input.IParameter;
import org.wdbuilder.plugin.icon.IconBlockPluginFacade.Parameter;
import org.wdbuilder.plugin.icon.domain.IconBlock;

class EditFormFactory extends UIExistingBlockFormFactory {

	EditFormFactory(String diagramKey, Block block) {
		super(diagramKey, block);
	}

	@Override
	public TwoColumnForm getForm() {
		if (!IconBlock.class.isInstance(block)) {
			return null;
		}

		final IconBlock iconBlock = IconBlock.class.cast(block);

		final PredefinedSelect<Icon> iconSelectField = new PredefinedSelect<Icon>(
				Icon.values(), Icon.Avatar);

		final TwoColumnForm form = new TwoColumnForm("edit-icon-block-save")
				.addHiddenField(BlockParameter.DiagramKey, diagramKey)
				.addReadOnlyField(BlockParameter.BlockKey, block.getKey())
				.addTextField(BlockParameter.Name, block.getName())
				.addSelectField(Parameter.IconID,
						String.valueOf(iconBlock.getIcon()), iconSelectField);

		return form;
	}

	@Override
	public String getSubmitCall() {
		StringBuilder sb = new StringBuilder(128).append("submitEditBlock('")
				.append(diagramKey).append("','").append(block.getKey())
				.append("',");
		appendFieldNames(sb, getParameters());
		sb.append(')');
		return sb.toString();
	}

	@Override
	public String getTitle() {
		return "Edit Icon Block";
	}

	private static Iterable<IParameter> getParameters() {
		List<IParameter> result = new ArrayList<IParameter>(2);
		result.add(BlockParameter.Name);
		result.add(Parameter.IconID);
		return result;
	}

}
