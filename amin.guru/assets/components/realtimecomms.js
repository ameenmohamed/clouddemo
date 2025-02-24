class RealtimeComms extends HTMLElement {
    constructor() {
      super();
    }

    connectedCallback() {
        this.innerHTML = `
        
        
        `;
    }
  }
  
  customElements.define('realtimecomms-component', RealtimeComms);