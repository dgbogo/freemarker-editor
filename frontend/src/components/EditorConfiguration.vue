<template>
  <div>
    <div class="navbar">&nbsp;</div>
    <form class="p-3">
      <div class="mb-3">
        <label for="templateFilePath" class="form-label"
          >Template file path</label
        >
        <input
          type="text"
          class="form-control"
          id="templateFilePath"
          v-model="config.templateFilePath"
          placeholder="Full file path for the template being edited"
        />
      </div>
      <div class="mb-3">
        <label for="dataFilePath" class="form-label">Data file path</label>
        <input
          type="text"
          class="form-control"
          id="dataFilePath"
          v-model="config.dataFilePath"
          placeholder="Full file path for the data used inside the template"
        />
      </div>
      <div class="mb-3">
        <label for="watchFolder" class="form-label">Watch folder</label>
        <input
          type="text"
          class="form-control"
          id="watchFolder"
          v-model="config.watchFolder"
          placeholder="Full path for the folder being watched (recursively)"
        />
      </div>
      <div class="mb-3">
        <label for="renderingMode" class="form-label">Rendering mode</label>
        <input
          type="text"
          class="form-control"
          id="renderingMode"
          v-model="config.renderingMode"
          placeholder="Rendering mode (0=HTML, 1=PDF)"
        />
      </div>
      <div class="mb-3">
        <label class="form-label">Custom fonts</label>
        <div
          v-for="font in config.customFonts"
          :key="font.path"
          class="row mb-3"
        >
          <div class="col-3">
            <input
              type="text"
              class="form-control"
              placeholder="Font name"
              disabled
              :value="font.name"
            />
          </div>
          <div class="col-8">
            <input
              type="text"
              class="form-control"
              placeholder="Font path"
              disabled
              :value="font.path"
            />
          </div>
          <div class="col-1 d-flex">
            <button
              type="button"
              class="btn btn-danger"
              @click="removeCustomFont(font)"
            >
              <span class="fa fa-minus" />
            </button>
          </div>
        </div>
        <div v-if="newFont.editing" class="row mb-3">
          <div class="col-3">
            <input
              type="text"
              class="form-control"
              placeholder="Font name"
              v-model="newFont.name"
            />
          </div>
          <div class="col-8">
            <input
              type="text"
              class="form-control"
              placeholder="Font path"
              v-model="newFont.path"
            />
          </div>
          <div class="col-1 d-flex">
            <button
              type="button"
              class="btn btn-primary"
              @click="addCustomFont"
            >
              <span class="fa fa-plus" />
            </button>
          </div>
        </div>
        <div v-else>
          <button
            type="button"
            class="btn btn-secondary"
            @click="newFont.editing = true"
          >
            Add custom font
          </button>
        </div>
      </div>
      <div class="actions d-flex">
        <button type="button" class="btn btn-primary" @click="saveConfig(true)">
          Save config
        </button>
        <button type="button" class="btn btn-danger" @click="$router.go(-1)">
          Cancel
        </button>
      </div>
    </form>
  </div>
</template>

<script>
export default {
  name: "EditorConfigration",
  data() {
    return {
      config: {
        templateFilePath: "",
        dataFilePath: "",
        watchFolder: "",
        renderingMode: 0,
        customFonts: [],
      },
      newFont: {
        editing: false,
        path: "",
        name: "",
      },
    };
  },
  methods: {
    saveConfig(goBack) {
      console.log("Saving current configuration");
      localStorage.setItem(
        "freemarker-editor-config",
        JSON.stringify(this.config)
      );
      if (goBack) {
        this.$router.go(-1);
      }
    },
    loadConfig() {
      const savedConfig = JSON.parse(
        localStorage.getItem("freemarker-editor-config")
      );
      if (savedConfig) {
        console.log("Loading config", savedConfig);
        Object.assign(this.config, savedConfig);
      } else {
        console.log("No config to be loaded; Saving default");
        this.saveConfig();
      }
    },
    addCustomFont() {
      this.config.customFonts.push({
        path: this.newFont.path,
        name: this.newFont.name,
      });
      console.log("Fonts", this.config.customFonts);
      this.newFont.name = "";
      this.newFont.path = "";
      this.newFont.editing = false;
    },
    removeCustomFont(font) {
      this.config.customFonts.splice(this.config.customFonts.indexOf(font), 1);
    },
  },
  mounted() {
    this.loadConfig();
  },
};
</script>

<style scoped>
.actions {
  justify-content: space-evenly;
  gap: 1rem;
}
.actions .btn {
  flex-grow: 1;
}
.navbar-ph {
  min-height: 37px;
  background-color: #ddd;
}
</style>