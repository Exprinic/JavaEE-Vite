import apiClient from "../client/apiClient.js";

export const errorApi = {
    uploadError: (error) => {
        return new apiClient.post('/error', error);
    }
}