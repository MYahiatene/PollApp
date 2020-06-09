import Vue from 'vue'
export const state = () => ({
    Poll: {},
    IDsToLoad: {
        pollID: 0,
        categoryID: 0,
        questionID: 0,
    },
})

export const getters = {
    getPoll(state) {
        return state.Poll
    },
    questionToLoad(state) {
        if (state.IDsToLoad !== undefined && state.IDsToLoad.questionID !== undefined) {
            return state.IDsToLoad.questionID
        }
        return 0
    },
    getQuestion(state) {
        if (getters.questionToLoad(state) === 0) {
            return null
        }
        const indices = getters.getIndices(state)
        if (indices !== null) {
            return state.Poll.categoryList[indices.c].questionList[indices.q]
        }
        return null
    },
    /* Id's to indices */
    getIndices(state) {
        if (state.IDsToLoad === undefined) {
            return null
        }
        const noQuestion = -1
        const categories = state.Poll.categoryList
        for (let c = 0; c < categories.length; c++) {
            if (categories[c].categoryId === state.IDsToLoad.categoryID) {
                const questions = categories[c].questionList
                for (let q = 0; q < questions.length; q++) {
                    if (questions[q].questionId === state.IDsToLoad.questionID) {
                        return { c, q }
                    }
                }
                return { c, noQuestion }
            }
        }
        return null
    },
    getCategory(state) {
        return (categoryID) => {
            for (let i = 0; i < state.Poll.categoryList.length; i++) {
                if (state.Poll.categoryList[i].categoryId === categoryID) {
                    return state.Poll.categoryList[i]
                }
            }
        }
    },
}

export const mutations = {
    initializeData(state, data) {
        state.Poll = data.data
        while (state.Poll.categoryList.length > 1) {
            delete state.Poll.categoryList.pop()
        }
    },
    saveData(state, data) {
        state.Poll = data
    },
    setPollID(state, id) {
        state.IDsToLoad.pollID = id
    },
    setToLoad(state, ids) {
        mutations.resetToLoad(state)
        state.IDsToLoad = ids
        const indices = getters.getIndices(state)
        if (indices === null) {
            state.IDsToLoad.categoryID = 0
            state.IDsToLoad.questionID = 0
        }
    },
    resetToLoad(state) {
        // vielleicht ein guter Punkt den Server zu aktuallisieren, hier wird eine Frage nicht länger bearbeitet
        state.IDsToLoad.categoryID = 0
        state.IDsToLoad.questionID = 0
    },
    setPollName(state, name) {
        state.Poll.pollName = name
    },
    addCategory(state, newCategory) {
        state.Poll.categoryList.push(newCategory)
    },
    removeCategory(state, categoryId) {
        for (let i = 0; i < state.Poll.categoryList.length; i++) {
            if (state.Poll.categoryList[i].categoryId === categoryId) {
                state.Poll.categoryList.splice(i, 1)
            }
        }
    },
    updateCategoryOrder(state, newList) {
        // Drag & Drop für Kategorien
        state.Poll.categoryList = newList
    },
    setCategoryName(state, category) {
        for (let i = 0; i < state.Poll.categoryList.length; i++) {
            if (state.Poll.categoryList[i].categoryId === category.categoryID) {
                state.Poll.categoryList[i].categoryName = category.name
            }
        }
    },
    addQuestion(state, { newId, categoryID }) {
        if (state.Poll.pollId === state.IDsToLoad.pollID) {
            const newQuestion = { questionId: newId, questionMessage: '', questionType: '', answerPossibilities: [''] }
            for (let i = 0; i < state.Poll.categoryList.length; i++) {
                if (state.Poll.categoryList[i].categoryId === categoryID) {
                    state.Poll.categoryList[i].questionList.push(newQuestion)
                }
            }
        }
    },
    removeQuestion(state) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Poll.categoryList[indices.c].questionList.splice(indices.q, 1)
            mutations.resetToLoad(state)
        }
    },
    updateQuestionOrder(state, payload) {
        // Drag & Drop für Fragen
        for (let i = 0; i < state.Poll.categoryList.length; i++) {
            if (state.Poll.categoryList[i].categoryId === payload.categoryID) {
                state.Poll.categoryList[i].questionList = payload.questions
            }
        }
    },
    setQuestionMessage(state, message) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Poll.categoryList[indices.c].questionList[indices.q].questionMessage = message
        }
    },
    setQuestionType(state, type) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Poll.categoryList[indices.c].questionList[indices.q].questionType = type
        }
    },
    setNrOfPossibleAnswers(state, number) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Poll.categoryList[indices.c].questionList[indices.q].numberOfPossibleAnswers = number
        }
    },
    setUserAnswers(state, active) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Poll.categoryList[indices.c].questionList[indices.q].userAnswers = active
        }
    },
    updateAnswer(state, payload) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            const answers = state.Poll.categoryList[indices.c].questionList[indices.q].answerPossibilities
            answers[payload.index] = payload.text
            let endIndex = answers.length - 1
            if (answers[endIndex].length > 0) {
                answers.push('')
            } else {
                endIndex--
                while (endIndex > 0 && answers[endIndex].length === 0) {
                    answers.pop()
                    endIndex--
                }
            }
        }
    },
    sendQuestion(state) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            const payload = {
                pollId: state.IDsToLoad.pollID,
                question: state.Poll.categoryList[indices.c].questionList[indices.q],
            }
            // weiß nicht ob das hier geht, aber solange es kein await braucht...
            this.$axios.post('/poll/' + payload.pollId + '/addquestion', payload).catch()
        }
    },
    setIntervalStart(state, value) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            const question = state.Poll.categoryList[indices.c].questionList[indices.q]
            if (question.intervalStart === undefined) {
                Vue.set(question, 'intervalStart', value)
            } else {
                question.intervalStart = value
            }
        }
    },
    setIntervalFinish(state, value) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            const question = state.Poll.categoryList[indices.c].questionList[indices.q]
            if (question.intervalFinish === undefined) {
                Vue.set(question, 'intervalFinish', value)
            } else {
                question.intervalFinish = value
            }
        }
    },
    setIntervalStep(state, value) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Poll.categoryList[indices.c].questionList[indices.q].intervalStep = value
        }
    },
    setIntervalLowerText(state, value) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Poll.categoryList[indices.c].questionList[indices.q].intervalLowerText = value
        }
    },
    setIntervalUpperText(state, value) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Poll.categoryList[indices.c].questionList[indices.q].intervalUpperText = value
        }
    },
    setIntervalNoAnswer(state, value) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Poll.categoryList[indices.c].questionList[indices.q].intervalNoAnswer = value
        }
    },
    setTextMultiline(state, value) {
        const indices = getters.getIndices(state)
        if (indices !== null) {
            state.Poll.categoryList[indices.c].questionList[indices.q].textMultiline = value
        }
    },
    setTextMin(state, value) {
        const indices = getters.getIndices(state)
        const question = state.Poll.categoryList[indices.c].questionList[indices.q]
        if (question.textMinimum === undefined) {
            Vue.set(question, 'textMinimum', value)
        } else {
            question.textMinimum = value
        }
    },
    setTextMax(state, value) {
        const indices = getters.getIndices(state)
        const question = state.Poll.categoryList[indices.c].questionList[indices.q]
        if (question.textMaximum === undefined) {
            Vue.set(question, 'textMaximum', value)
        } else {
            question.textMaximum = value
        }
    },
}
export const actions = {
    async initialize({ state, commit }) {
        const data = await this.$axios.get('/poll/' + state.IDsToLoad.pollID)
        commit('resetToLoad')
        commit('initializeData', data)
    },
    async createCategory({ state, commit }) {
        let error = ''
        const newId = await this.$axios
            .post('/poll/' + state.IDsToLoad.pollID + '/addcategory', {
                pollId: state.IDsToLoad.pollID,
                name: 'Neue Kategorie',
            })
            .catch((reason) => {
                error = reason
            })
        if (error.length === 0) {
            const newCategory = {
                categoryId: newId,
                categoryName: 'Neue Kategorie',
                pollId: state.IDsToLoad.pollID,
                questionList: [],
            }
            commit('addCategory', newCategory)
        } else {
            alert('Kategorie konnte nicht erstellt werden\n' + error)
        }
    },
    async deleteCategory({ commit, state }, categoryID) {
        if (confirm('Soll diese Kategorie wirklich gelöscht werden?')) {
            commit('removeCategory')
            let error = ''
            await this.$axios
                .post('/poll/' + state.IDsToLoad.pollId + '/removecategory/' + categoryID)
                .catch((reason) => {
                    error = reason
                })
            if (error.length === 0) {
                commit('removeCategory', categoryID)
            } else {
                alert('Kategorie konnte nicht gelöscht werden\n' + error)
            }
        }
    },
    async createQuestion({ commit, state }, categoryID) {
        const payload = {
            pollId: state.IDsToLoad.pollID,
            // categoryId: categoryID,
            questionMessage: '',
            answerPossibilities: [''],
            questionType: '',
        }
        let error = ''
        const response = await this.$axios
            .post('/poll/' + payload.pollId + /* '/' + payload.categoryId + '/' + */ '/addquestion', payload)
            .catch((reason) => {
                error = reason
            })
        if (error.length === 0) {
            const newId = response.data
            commit('addQuestion', { newId, categoryID })
        } else {
            alert('Frage konnte nicht erstellt werden\n' + error)
        }
    },
    async deleteQuestion({ commit, state }) {
        if (confirm('Soll diese Frage wirklich gelöscht werden?')) {
            const payload = {
                pollId: state.IDsToLoad.pollID,
                questionId: state.IDsToLoad.questionID,
            }
            let error = ''
            await this.$axios
                .post('/poll/' + payload.pollId + '/removequestion/' + payload.questionId)
                .catch((reason) => {
                    error = reason
                })
            if (error.length === 0) {
                commit('removeQuestion')
            } else {
                alert('Frage konnte nicht gelöscht werden\n' + error)
            }
        }
    },
}
