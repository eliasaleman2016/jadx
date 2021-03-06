package jadx.dex.trycatch;

import jadx.Consts;
import jadx.dex.info.ClassInfo;
import jadx.dex.instructions.args.InsnArg;
import jadx.dex.nodes.BlockNode;
import jadx.dex.nodes.IContainer;
import jadx.utils.InsnUtils;

import java.util.ArrayList;
import java.util.List;

public class ExceptionHandler {

	private final ClassInfo catchType;
	private final int handleOffset;

	private BlockNode handleBlock;
	private final List<BlockNode> blocks = new ArrayList<BlockNode>();
	private IContainer handlerRegion;
	private InsnArg arg;

	public ExceptionHandler(int addr, ClassInfo type) {
		this.handleOffset = addr;
		this.catchType = type;
	}

	public ClassInfo getCatchType() {
		return catchType;
	}

	public boolean isCatchAll() {
		return catchType == null || catchType.getFullName().equals(Consts.CLASS_THROWABLE);
	}

	public int getHandleOffset() {
		return handleOffset;
	}

	public BlockNode getHandleBlock() {
		return handleBlock;
	}

	public void setHandleBlock(BlockNode handleBlock) {
		this.handleBlock = handleBlock;
	}

	public List<BlockNode> getBlocks() {
		return blocks;
	}

	public void addBlock(BlockNode node) {
		blocks.add(node);
	}

	public IContainer getHandlerRegion() {
		return handlerRegion;
	}

	public void setHandlerRegion(IContainer handlerRegion) {
		this.handlerRegion = handlerRegion;
	}

	public InsnArg getArg() {
		return arg;
	}

	public void setArg(InsnArg arg) {
		this.arg = arg;
	}

	@Override
	public int hashCode() {
		return (catchType == null ? 0 : 31 * catchType.hashCode()) + handleOffset;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ExceptionHandler other = (ExceptionHandler) obj;
		if (catchType == null) {
			if (other.catchType != null) return false;
		} else if (!catchType.equals(other.catchType)) return false;
		return handleOffset == other.handleOffset;
	}

	@Override
	public String toString() {
		return (catchType == null ? "all" 
            : catchType.getShortName()) + " -> " + InsnUtils.formatOffset(handleOffset);
	}

}
