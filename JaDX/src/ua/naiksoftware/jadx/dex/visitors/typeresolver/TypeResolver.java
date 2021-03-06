package jadx.dex.visitors.typeresolver;

import jadx.dex.attributes.BlockRegState;
import jadx.dex.instructions.args.InsnArg;
import jadx.dex.instructions.args.RegisterArg;
import jadx.dex.nodes.BlockNode;
import jadx.dex.nodes.InsnNode;
import jadx.dex.nodes.MethodNode;
import jadx.dex.visitors.AbstractVisitor;

import java.util.List;

public class TypeResolver extends AbstractVisitor {

	@Override
	public void visit(MethodNode mth) {
		if (mth.isNoCode())
			return;

		visitBlocks(mth);
		visitEdges(mth);

		// clear register states
		for (BlockNode block : mth.getBasicBlocks()) {
			block.setStartState(null);
			block.setEndState(null);
		}
	}

	private void visitBlocks(MethodNode mth) {
		for (BlockNode block : mth.getBasicBlocks()) {
			BlockRegState state = new BlockRegState(mth);

			if (block == mth.getEnterBlock()) {
				for (RegisterArg arg : mth.getArguments(true)) {
					state.assignReg(arg);
				}
			}
			block.setStartState(new BlockRegState(state));

			for (InsnNode insn : block.getInstructions()) {
				for (InsnArg arg : insn.getArguments()) {
					if (arg.isRegister())
						state.use((RegisterArg) arg);
				}
				if (insn.getResult() != null)
					state.assignReg(insn.getResult());
			}

			if (block.getSuccessors().size() > 0)
				block.setEndState(new BlockRegState(state));
		}
	}

	private void visitEdges(MethodNode mth) {
		List<BlockNode> preds = mth.getBasicBlocks();
		boolean changed;
		do {
			changed = false;
			for (BlockNode block : preds) {
				for (BlockNode pred : block.getPredecessors()) {
					if (connectEdges(mth, pred, block, true))
						changed = true;
				}
			}
		} while (changed);

		for (BlockNode block : mth.getBasicBlocks()) {
			for (BlockNode dest : block.getSuccessors()) {
				connectEdges(mth, block, dest, false);
			}
		}
	}

	private boolean connectEdges(MethodNode mth, BlockNode from, BlockNode to, boolean back) {
		BlockRegState end = from.getEndState();
		BlockRegState start = to.getStartState();

		boolean changed = false;
		for (int r = 0; r < mth.getRegsCount(); r++) {
			RegisterArg sr = start.getRegister(r);
			RegisterArg er = end.getRegister(r);

			if (back) {
				if (er.getTypedVar() == null && sr.getTypedVar() != null) {
					er.replace(sr);
					changed = true;
				}
			} else {
				if (sr.getTypedVar() != null && er.getTypedVar() != null) {
					sr.replace(er);
					changed = true;
				}
			}
		}
		return changed;
	}
}
