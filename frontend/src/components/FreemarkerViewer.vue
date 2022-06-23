<template>
  <div>
    <div class="statusbar">
      <!-- Connect/disconnect button -->
      <div v-if="connected" class="clickable" @click="stopWebsocket()">
        <i class="fa-solid fa-bolt connected" />
      </div>
      <div v-else class="clickable" @click="startWebsocket()">
        <i class="fa-solid fa-bolt disconnected" />
      </div>
      <!-- Restart websocket -->
      <div
        class="clickable"
        :class="{ disabled: !connected }"
        @click="restartWebsocket(!connected)"
      >
        <i class="fa-solid fa-arrows-rotate"></i>
      </div>
      <div class="clickable" @click="$router.push({ name: 'config' })">
        <i class="fa-solid fa-gear" />
      </div>
      <div class="clickable menu-item" @click="changeRenderingMode()">
        <template v-if="config.renderingMode == 0">
          <i class="fa-brands fa-html5" /> HTML
        </template>
        <template v-else-if="config.renderingMode == 1">
          <i class="fa-solid fa-file-pdf" /> PDF
        </template>
      </div>

      <div class="separator" />

      <div class="menu-item">
        <i class="fa-solid fa-file-code" />
        {{ getFilename(this.config.templateFilePath) }}
      </div>
      <div class="menu-item">
        <i class="fa-solid fa-file-alt" />
        {{ getFilename(this.config.dataFilePath) }}
      </div>
      <div class="menu-item">
        <i class="fa-solid fa-eye" />
        {{ getFilename(this.config.watchFolder) }}
      </div>

      <div class="status-message">
        <template v-if="statusMessage">{{ statusMessage }}</template>
        <i v-else class="fa-solid fa-circle-notch fa-spin" />
      </div>
    </div>
    <template v-if="result">
      <div v-show="result.rerendering" />
      <iframe
        v-if="result.rendered && result.mode === 0"
        v-show="!result.rerendering"
        @load="frameRendered()"
        :srcdoc="result.rendered"
        class="render-frame"
        frameborder="0"
        height="100%"
        width="100%"
      />
      <iframe
        v-else-if="result.rendered && result.mode === 1"
        v-show="!result.rerendering"
        @load="frameRendered()"
        :src="'data:application/pdf;base64,' + result.rendered"
        class="render-frame"
        frameborder="0"
        height="100%"
        width="100%"
      />
      <div v-else-if="result.error" class="render-error">
        <div>{{ result.error }}</div>
        <br />
        <b>Stack trace:</b>
        <div class="stack-trace" v-html="result.stackTrace" />
      </div>
    </template>
  </div>
</template>

<script>
export default {
  name: "FreemarkerViewer",
  data() {
    return {
      websocket: null,
      connected: false,
      result: {
        rerendering: false,
        rendered: null,
        timeToRenderMs: null,
        error: null,
        stackTrace: null,
        renderingMode: 0,
      },
      config: {
        templateFilePath: "",
        dataFilePath: "",
        watchFolder: "",
        renderingMode: 0,
      },
    };
  },
  computed: {
    statusMessage() {
      if (this.result.error) return "Error";
      if (this.result.rendered)
        return `Render time: ${this.result.timeToRenderMs}ms`;
      if (!this.connected) return "Disconnected";
      return "";
    },
  },
  methods: {
    startWebsocket() {
      console.log("Connecting...");
      this.websocket = new WebSocket("ws://localhost:8080/ws/test");

      this.websocket.onopen = () => {
        console.log("Connected");
        this.connected = true;
        this.websocket.send(JSON.stringify(this.config));
      };

      this.websocket.onmessage = (evt) => {
        const response = JSON.parse(evt.data);
        //console.log("Response", response);
        if (!response.error) {
          this.result.rerendering = true;
          setTimeout(() => {
            this.result.rerendering = false;
          }, 1500);
        }
        this.result.error = response.error;
        this.result.stackTrace = response.stackTrace;
        this.result.timeToRenderMs = response.timeToRenderMs;
        this.result.rendered = response.html;
        this.result.mode = response.mode;
      };

      this.websocket.onclose = () => {
        console.log("Connection closed");
        this.endConnection();
      };
    },
    stopWebsocket() {
      if (!this.connected) return;
      this.websocket.close();
    },
    endConnection() {
      this.connected = false;
      this.result.rendered = null;
      this.result.error = null;
    },
    restartWebsocket(disabled) {
      if (disabled) return;
      this.stopWebsocket();
      this.startWebsocket();
    },
    frameRendered() {
      this.result.rerendering = false;
    },
    getFilename(fullPath) {
      return fullPath.replace(/^.*[\\/]/, "");
    },
    changeRenderingMode() {
      this.config.renderingMode = this.config.renderingMode == 0 ? 1 : 0;
      this.restartWebsocket();
    },
    loadConfig() {
      const savedConfig = JSON.parse(
        localStorage.getItem("freemarker-editor-config")
      );
      if (savedConfig) {
        console.log("Loading config", savedConfig);
        Object.assign(this.config, savedConfig);
      } else {
        console.log(
          "No config to be loaded; Redirecting to configuration page"
        );
        this.$router.push({ name: "config" });
      }
    },
  },
  mounted() {
    this.loadConfig();
    this.startWebsocket();
  },
};
</script>

<style scoped>
.statusbar {
  border-bottom: 1px solid #aaa;
  background-color: #ddd;
  cursor: default;
}

.statusbar > div {
  display: inline-block;
  text-align: center;
}

.connected {
  color: green;
}

.disconnected {
  color: red;
}

.clickable {
  cursor: default;
  min-width: 36px;
  min-height: 36px;
  height: 36px;
  line-height: 36px;
}

.clickable.disabled {
  cursor: not-allowed;
  color: gray;
}

.clickable:hover {
  background-color: #bbb;
}

.clickable.disabled:hover {
  background-color: initial;
}

.menu-item {
  padding: 0 10px;
}

.menu-item > i {
  padding-right: 5px;
}

.render-frame {
  overflow: hidden;
  height: calc(100vh - 36px);
  width: 100%;
}

.render-error {
  padding: 7px;
}

.stack-trace {
  font-size: 12px;
  color: rgb(119, 0, 0);
}

.status-message {
  float: right;
  padding-right: 7px;
  line-height: 36px;
}

.separator {
  width: 1rem;
}
</style>
