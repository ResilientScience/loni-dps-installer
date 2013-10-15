
SCRIPT_DIR=$(CURDIR)/install_files

TARGETS=

ifeq ($(INSTALL_PIPELINE),true)
	TARGETS += mk_pipeline
endif

ifeq ($(INSTALL_SGE),true)
	TARGETS += mk_sge
endif

ifeq ($(INSTALL_NI_TOOLS),true)
	TARGETS += mk_ni_tools
endif

ifeq ($(INSTALL_BI_TOOLS),true)
	TARGETS += mk_bi_tools
endif

all: $(TARGETS)
	
mk_sge:
	echo "Installing SGE"
	$(SCRIPT_DIR)/installSGE.sh

mk_pipeline:
	echo "Installing Pipeline"
	$(SCRIPT_DIR)/installPipeline.sh
	
mk_ni_tools:
	echo "Installing NI tools"
	$(SCRIPT_DIR)/installNITools.sh

mk_bi_tools:
	echo "Installing Bioinformatics tools"
	$(SCRIPT_DIR)/installBioinformaticsTools.sh
	